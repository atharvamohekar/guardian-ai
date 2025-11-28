package com.guardianai.app.feature.agent

import com.guardianai.app.domain.models.*
import com.guardianai.app.data.repository.VitalsRepository
import com.guardianai.app.data.repository.PredictionIncidentRepository
import com.guardianai.app.data.repository.EmergencyLogRepository
import com.guardianai.app.data.datastore.PreferencesDataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthAgentService @Inject constructor(
    private val vitalsRepository: VitalsRepository,
    private val predictionRepository: PredictionIncidentRepository,
    private val emergencyRepository: EmergencyLogRepository,
    private val preferencesDataStore: PreferencesDataStore
) {

    private val _alertState = MutableSharedFlow<AlertState>(replay = 0)
    val alertState: SharedFlow<AlertState> = _alertState.asSharedFlow()

    private val _verificationState = MutableStateFlow<VerificationState?>(null)
    val verificationState: StateFlow<VerificationState?> = _verificationState.asStateFlow()

    // Threshold values (can be customized per user)
    private val heartRateThreshold = 100 // bpm
    private val spO2Threshold = 94 // %
    private val stressScoreThreshold = 60
    private val sleepHoursThreshold = 6f // hours

    // Verification workflow
    private val verificationReadings = mutableListOf<VitalsSample>()

    fun startHealthMonitoring() {
        GlobalScope.launch {
            // Listen to vitals data
            vitalsRepository.getLatestVitals().collect { vitals ->
                processVitalsSample(vitals)
            }
        }
    }

    private suspend fun processVitalsSample(vitals: VitalsSample?) {
        if (vitals == null) return

        // Check if predictions are paused
        val isPaused = preferencesDataStore.isPredictionsPaused().first()
        if (isPaused) return

        // Evaluate prediction rules
        val prediction = evaluatePredictionRules(vitals)
        if (prediction != null) {
            handleNewPrediction(prediction, vitals)
        }
    }

    private fun evaluatePredictionRules(vitals: VitalsSample): PredictionResult? {
        val anomalies = mutableListOf<AnomalyDetection>()

        // Heart Rate Analysis
        if (vitals.heartRate > heartRateThreshold) {
            val severity = when {
                vitals.heartRate > 120 -> Severity.HIGH
                vitals.heartRate > 110 -> Severity.ELEVATED
                else -> Severity.MODERATE
            }
            anomalies.add(
                AnomalyDetection(
                    metricType = MetricType.HEART_RATE,
                    currentValue = vitals.heartRate.toFloat(),
                    thresholdValue = heartRateThreshold.toFloat(),
                    severity = severity,
                    description = "Heart rate ${vitals.heartRate} bpm exceeds threshold of $heartRateThreshold bpm"
                )
            )
        }

        // SpO2 Analysis
        if (vitals.spO2 < spO2Threshold) {
            val severity = when {
                vitals.spO2 < 88 -> Severity.CRITICAL
                vitals.spO2 < 92 -> Severity.HIGH
                else -> Severity.ELEVATED
            }
            anomalies.add(
                AnomalyDetection(
                    metricType = MetricType.SPO2,
                    currentValue = vitals.spO2.toFloat(),
                    thresholdValue = spO2Threshold.toFloat(),
                    severity = severity,
                    description = "SpO2 ${vitals.spO2}% below threshold of $spO2Threshold%"
                )
            )
        }

        // Stress Score Analysis
        if (vitals.stressScore > stressScoreThreshold) {
            val severity = when {
                vitals.stressScore > 80 -> Severity.CRITICAL
                vitals.stressScore > 70 -> Severity.HIGH
                else -> Severity.ELEVATED
            }
            anomalies.add(
                AnomalyDetection(
                    metricType = MetricType.STRESS_SCORE,
                    currentValue = vitals.stressScore.toFloat(),
                    thresholdValue = stressScoreThreshold.toFloat(),
                    severity = severity,
                    description = "Stress score ${vitals.stressScore} exceeds threshold of $stressScoreThreshold"
                )
            )
        }

        // Sleep Hours Analysis
        if (vitals.sleepHours < sleepHoursThreshold) {
            val severity = when {
                vitals.sleepHours < 4f -> Severity.HIGH
                vitals.sleepHours < 5f -> Severity.ELEVATED
                else -> Severity.MODERATE
            }
            anomalies.add(
                AnomalyDetection(
                    metricType = MetricType.SLEEP_HOURS,
                    currentValue = vitals.sleepHours,
                    thresholdValue = sleepHoursThreshold,
                    severity = severity,
                    description = "Sleep duration ${vitals.sleepHours}h below recommended minimum of ${sleepHoursThreshold}h"
                )
            )
        }

        return if (anomalies.isNotEmpty()) {
            val primaryAnomaly = anomalies.maxByOrNull { it.severity.level }
            PredictionResult(
                incidentType = primaryAnomaly?.let { anomaly ->
                    when (anomaly.metricType) {
                        MetricType.HEART_RATE -> IncidentType.ELEVATED_HEART_RATE
                        MetricType.SPO2 -> IncidentType.LOW_SPO2
                        MetricType.STRESS_SCORE -> IncidentType.HIGH_STRESS_LEVEL
                        MetricType.SLEEP_HOURS -> IncidentType.SLEEP_DEPRIVATION
                        else -> IncidentType.ELEVATED_HEART_RATE
                    }
                } ?: IncidentType.ELEVATED_HEART_RATE,
                severity = primaryAnomaly?.severity ?: Severity.MODERATE,
                anomalies = anomalies,
                requiresVerification = true
            )
        } else null
    }

    private suspend fun handleNewPrediction(prediction: PredictionResult, vitals: VitalsSample) {
        if (prediction.requiresVerification) {
            // Start verification workflow
            startVerificationWorkflow(prediction, vitals)
        } else {
            // Direct alert
            triggerAlert(prediction, vitals)
        }
    }

    private suspend fun startVerificationWorkflow(prediction: PredictionResult, initialVitals: VitalsSample) {
        verificationReadings.clear()
        verificationReadings.add(initialVitals)

        _verificationState.value = VerificationState(
            incidentId = "prediction_${System.currentTimeMillis()}",
            predictionResult = prediction,
            currentStep = 1,
            totalSteps = 3,
            stepStartTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toInstant().toEpochMilliseconds(),
            isVerificationComplete = false
        )

        _alertState.emit(AlertState.VerificationStarted)
    }

    suspend fun submitVerificationReading(vitals: VitalsSample) {
        verificationReadings.add(vitals)

        val currentState = _verificationState.value ?: return
        val newState = currentState.copy(
            currentStep = currentState.currentStep + 1,
            stepStartTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toInstant().toEpochMilliseconds()
        )

        _verificationState.value = newState
        verificationReadings.add(vitals)

        // Check if verification is complete
        if (currentState.currentStep >= currentState.totalSteps) {
            completeVerification(currentState.predictionResult)
        }
    }

    private suspend fun completeVerification(prediction: PredictionResult) {
        // Verify if anomaly persists across all readings
        val anomalyPersists = verificationReadings.all { reading ->
            isAnomalyPresent(reading, prediction)
        }

        if (anomalyPersists) {
            triggerAlert(prediction, verificationReadings.last())
        } else {
            // False alarm - cancel
            _alertState.emit(AlertState.VerificationCancelled)
            _verificationState.value = null
        }
    }

    private fun isAnomalyPresent(vitals: VitalsSample, prediction: PredictionResult): Boolean {
        return prediction.anomalies.any { anomaly ->
            when (anomaly.metricType) {
                MetricType.HEART_RATE -> vitals.heartRate > heartRateThreshold
                MetricType.SPO2 -> vitals.spO2 < spO2Threshold
                MetricType.STRESS_SCORE -> vitals.stressScore > stressScoreThreshold
                MetricType.SLEEP_HOURS -> vitals.sleepHours < sleepHoursThreshold
                else -> false
            }
        }
    }

    private suspend fun triggerAlert(prediction: PredictionResult, vitals: VitalsSample) {
        val incidentId = "incident_${System.currentTimeMillis()}"
        val timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toInstant().toEpochMilliseconds()

        // Create prediction incident
        val detectedMetrics = prediction.anomalies.map { anomaly ->
            DetectedMetric(
                metricType = anomaly.metricType,
                currentValue = anomaly.currentValue,
                thresholdValue = anomaly.thresholdValue,
                deviationPercentage = ((anomaly.currentValue - anomaly.thresholdValue) / anomaly.thresholdValue) * 100f,
                trendDirection = TrendDirection.INCREASING
            )
        }

        val recommendations = generateRecommendations(prediction.anomalies)

        val incident = PredictionIncidentEntity(
            id = incidentId,
            userId = 1,
            timestamp = timestamp,
            incidentType = prediction.incidentType.name,
            severity = prediction.severity.name,
            detectedMetricsJson = com.google.gson.Gson().toJson(detectedMetrics),
            explanation = "Multiple anomalies detected: ${prediction.anomalies.joinToString { it.description }}",
            recommendationsJson = com.google.gson.Gson().toJson(recommendations),
            verificationReadingsJson = com.google.gson.Gson().toJson(verificationReadings.map { it.id }),
            resolved = false,
            escalated = false,
            emergencyWorkflowTriggered = false
        )

        predictionRepository.insertIncident(incident)

        // Emit alert state
        _alertState.emit(AlertState.AlertTriggered(
            incidentId = incidentId,
            severity = prediction.severity,
            vitals = vitals,
            recommendations = recommendations
        ))

        _verificationState.value = null
    }

    private fun generateRecommendations(anomalies: List<AnomalyDetection>): List<String> {
        val recommendations = mutableListOf<String>()

        anomalies.forEach { anomaly ->
            when (anomaly.metricType) {
                MetricType.HEART_RATE -> {
                    recommendations.add("Monitor heart rate closely for next 30 minutes")
                    recommendations.add("Avoid strenuous activities")
                    recommendations.add("Consider contacting your doctor if persists")
                }
                MetricType.SPO2 -> {
                    recommendations.add("Check oxygen saturation levels regularly")
                    recommendations.add("Ensure proper ventilation")
                    recommendations.add("Seek medical attention if levels drop further")
                }
                MetricType.STRESS_SCORE -> {
                    recommendations.add("Practice deep breathing exercises")
                    recommendations.add("Take a short break from current activity")
                    recommendations.add("Consider stress management techniques")
                }
                MetricType.SLEEP_HOURS -> {
                    recommendations.add("Prioritize getting adequate sleep tonight")
                    recommendations.add("Maintain consistent sleep schedule")
                    recommendations.add("Avoid caffeine late in the day")
                }
                else -> {}
            }
        }

        return recommendations.distinct()
    }

    fun cancelCurrentAlert() {
        GlobalScope.launch {
            _alertState.emit(AlertState.AlertCancelled)
            _verificationState.value = null
        }
    }

    fun acknowledgeAlert() {
        GlobalScope.launch {
            _alertState.emit(AlertState.AlertAcknowledged)
        }
    }
}

// Data classes for agent state
data class PredictionResult(
    val incidentType: IncidentType,
    val severity: Severity,
    val anomalies: List<AnomalyDetection>,
    val requiresVerification: Boolean
)

data class AnomalyDetection(
    val metricType: MetricType,
    val currentValue: Float,
    val thresholdValue: Float,
    val severity: Severity,
    val description: String
)

data class VerificationState(
    val incidentId: String,
    val predictionResult: PredictionResult,
    val currentStep: Int,
    val totalSteps: Int,
    val stepStartTime: Long,
    val isVerificationComplete: Boolean
)

sealed class AlertState {
    object Idle : AlertState()
    object VerificationStarted : AlertState()
    object VerificationCancelled : AlertState()
    data class AlertTriggered(
        val incidentId: String,
        val severity: Severity,
        val vitals: VitalsSample,
        val recommendations: List<String>
    ) : AlertState()
    object AlertAcknowledged : AlertState()
    object AlertCancelled : AlertState()
}

// Extension property for severity level
val Severity.level: Int
    get() = when (this) {
        Severity.LOW -> 1
        Severity.MODERATE -> 2
        Severity.ELEVATED -> 3
        Severity.HIGH -> 4
        Severity.CRITICAL -> 5
    }