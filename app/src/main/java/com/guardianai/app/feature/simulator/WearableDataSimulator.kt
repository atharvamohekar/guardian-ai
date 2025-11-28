package com.guardianai.app.feature.simulator

import com.guardianai.app.domain.models.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Duration.Companion.minutes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WearableDataSimulator @Inject constructor() {

    private val _vitalsFlow = MutableSharedFlow<VitalsSample>()
    val vitalsFlow: SharedFlow<VitalsSample> = _vitalsFlow

    private var isRunning = false
    private var compressionFactor = 30 // 15 minutes -> 30 seconds by default
    private var currentScenario = AnomalyScenario.NORMAL

    // Baseline vitals for a healthy user
    private val baselineHeartRate = 72
    private val baselineSpO2 = 98
    private val baselineStressScore = 25
    private val baselineStepsPerHour = 850
    private val baselineSleepHours = 7.5f

    enum class AnomalyScenario {
        NORMAL,
        GRADUAL_HEART_RATE_INCREASE,
        SUDDEN_SPO2_DROP,
        STRESS_SPIKE_PATTERN,
        SLEEP_DEPRIVATION,
        MULTIPLE_ANOMALIES
    }

    fun startSimulation(userId: Int = 1) {
        if (!isRunning) {
            isRunning = true
            generateVitalsData(userId)
        }
    }

    fun stopSimulation() {
        isRunning = false
    }

    fun setScenario(scenario: AnomalyScenario) {
        currentScenario = scenario
    }

    fun setTimeCompressionFactor(factor: Int) {
        compressionFactor = factor.coerceIn(1, 1440) // 1x real-time to 24 hours compressed
    }

    fun injectAnomaly(scenario: AnomalyScenario, duration: Duration = minutes(45)) {
        setScenario(scenario)
        // Anomaly will be applied in the next vitals generation cycle
    }

    private fun generateVitalsData(userId: Int) {
        kotlinx.coroutines.GlobalScope.launch {
            while (isActive && isRunning) {
                val timestamp = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toInstant().toEpochMilliseconds()

                val vitals = generateVitalsSample(
                    userId = userId,
                    timestamp = timestamp,
                    scenario = currentScenario
                )

                _vitalsFlow.emit(vitals)

                // Wait based on compression factor
                val delayTime = when (compressionFactor) {
                    1 -> minutes(15) // Real-time: 15 minutes
                    30 -> seconds(30) // Fast demo: 30 seconds
                    60 -> seconds(10) // Very fast: 10 seconds
                    300 -> seconds(2)  // Ultra fast: 2 seconds
                    else -> seconds(30) // Default fast mode
                }

                delay(delayTime)
            }
        }
    }

    private fun generateVitalsSample(userId: Int, timestamp: Long, scenario: AnomalyScenario): VitalsSample {
        return when (scenario) {
            AnomalyScenario.NORMAL -> generateNormalVitals(userId, timestamp)
            AnomalyScenario.GRADUAL_HEART_RATE_INCREASE -> generateGradualHeartRateIncrease(userId, timestamp)
            AnomalyScenario.SUDDEN_SPO2_DROP -> generateSuddenSpO2Drop(userId, timestamp)
            AnomalyScenario.STRESS_SPIKE_PATTERN -> generateStressSpikePattern(userId, timestamp)
            AnomalyScenario.SLEEP_DEPRIVATION -> generateSleepDeprivation(userId, timestamp)
            AnomalyScenario.MULTIPLE_ANOMALIES -> generateMultipleAnomalies(userId, timestamp)
        }
    }

    private fun generateNormalVitals(userId: Int, timestamp: Long): VitalsSample {
        val random = Random(System.currentTimeMillis())

        return VitalsSample(
            id = "vitals_${timestamp}",
            userId = userId,
            timestamp = timestamp,
            heartRate = baselineHeartRate + random.nextInt(-8, 9),
            spO2 = baselineSpO2 + random.nextInt(-2, 2),
            stressScore = baselineStressScore + random.nextInt(-10, 10),
            steps = baselineStepsPerHour + random.nextInt(-100, 100),
            sleepHours = baselineSleepHours + random.nextFloat(-0.5f, 0.5f),
            dataSource = DataSource.SIMULATED,
            isAnomaly = false,
            anomalyType = null
        )
    }

    private fun generateGradualHeartRateIncrease(userId: Int, timestamp: Long): VitalsSample {
        val random = Random(System.currentTimeMillis())
        val minutesElapsed = (timestamp % (15 * compressionFactor * 60 * 1000)) / (60 * 1000)
        val increaseAmount = (minutesElapsed * 2f).toInt().coerceAtMost(50)

        val heartRate = baselineHeartRate + increaseAmount + random.nextInt(-5, 5)
        val isAnomaly = heartRate > 100

        return VitalsSample(
            id = "vitals_${timestamp}",
            userId = userId,
            timestamp = timestamp,
            heartRate = heartRate,
            spO2 = baselineSpO2 + random.nextInt(-1, 1),
            stressScore = baselineStressScore + increaseAmount / 2 + random.nextInt(-5, 5),
            steps = baselineStepsPerHour + random.nextInt(-50, 50),
            sleepHours = baselineSleepHours + random.nextFloat(-0.3f, 0.3f),
            dataSource = DataSource.SIMULATED,
            isAnomaly = isAnomaly,
            anomalyType = if (isAnomaly) AnomalyType.ELEVATED_HEART_RATE else null
        )
    }

    private fun generateSuddenSpO2Drop(userId: Int, timestamp: Long): VitalsSample {
        val random = Random(System.currentTimeMillis())
        val spO2 = if (random.nextFloat() < 0.3f) {
            88 + random.nextInt(-5, 3) // Sudden drop to dangerous levels
        } else {
            baselineSpO2 + random.nextInt(-3, 2)
        }
        val isAnomaly = spO2 < 94

        return VitalsSample(
            id = "vitals_${timestamp}",
            userId = userId,
            timestamp = timestamp,
            heartRate = baselineHeartRate + random.nextInt(-10, 15), // Heart rate increases in response to low O2
            spO2 = spO2,
            stressScore = baselineStressScore + (if (isAnomaly) 20 else 0) + random.nextInt(-8, 8),
            steps = baselineStepsPerHour + random.nextInt(-80, 30),
            sleepHours = baselineSleepHours + random.nextFloat(-0.4f, 0.2f),
            dataSource = DataSource.SIMULATED,
            isAnomaly = isAnomaly,
            anomalyType = if (isAnomaly) AnomalyType.LOW_SPO2 else null
        )
    }

    private fun generateStressSpikePattern(userId: Int, timestamp: Long): VitalsSample {
        val random = Random(System.currentTimeMillis())
        val stressMultiplier = when ((timestamp / (5 * compressionFactor * 1000)) % 4) {
            0 -> 1.0f
            1 -> 2.5f
            2 -> 3.0f
            3 -> 1.2f
        }

        val stressScore = (baselineStressScore * stressMultiplier).toInt()
        val isAnomaly = stressScore > 60

        return VitalsSample(
            id = "vitals_${timestamp}",
            userId = userId,
            timestamp = timestamp,
            heartRate = baselineHeartRate + (stressScore / 3) + random.nextInt(-8, 8),
            spO2 = baselineSpO2 + random.nextInt(-2, 2),
            stressScore = stressScore + random.nextInt(-5, 5),
            steps = baselineStepsPerHour + random.nextInt(-100, 50),
            sleepHours = baselineSleepHours + random.nextFloat(-0.5f, 0.5f),
            dataSource = DataSource.SIMULATED,
            isAnomaly = isAnomaly,
            anomalyType = if (isAnomaly) AnomalyType.HIGH_STRESS else null
        )
    }

    private fun generateSleepDeprivation(userId: Int, timestamp: Long): VitalsSample {
        val random = Random(System.currentTimeMillis())
        val sleepHours = baselineSleepHours - random.nextFloat(1.5f, 3.0f)
        val isAnomaly = sleepHours < 5.5f

        return VitalsSample(
            id = "vitals_${timestamp}",
            userId = userId,
            timestamp = timestamp,
            heartRate = baselineHeartRate + random.nextInt(-5, 15), // Irregular due to fatigue
            spO2 = baselineSpO2 + random.nextInt(-3, 2),
            stressScore = baselineStressScore + (if (isAnomaly) 25 else 5) + random.nextInt(-8, 8),
            steps = baselineStepsPerHour + random.nextInt(-150, 20), // Reduced activity
            sleepHours = sleepHours,
            dataSource = DataSource.SIMULATED,
            isAnomaly = isAnomaly,
            anomalyType = if (isAnomaly) AnomalyType.SLEEP_DEPRIVATION else null
        )
    }

    private fun generateMultipleAnomalies(userId: Int, timestamp: Long): VitalsSample {
        val random = Random(System.currentTimeMillis())

        // Complex scenario with multiple issues
        val heartRate = baselineHeartRate + 35 + random.nextInt(-10, 10)
        val spO2 = 91 + random.nextInt(-3, 2)
        val stressScore = 75 + random.nextInt(-10, 10)

        return VitalsSample(
            id = "vitals_${timestamp}",
            userId = userId,
            timestamp = timestamp,
            heartRate = heartRate,
            spO2 = spO2,
            stressScore = stressScore,
            steps = baselineStepsPerHour - 200 + random.nextInt(-50, 30),
            sleepHours = baselineSleepHours - 2.5f + random.nextFloat(-0.5f, 0.5f),
            dataSource = DataSource.SIMULATED,
            isAnomaly = true,
            anomalyType = AnomalyType.COMBINED_ANOMALY
        )
    }

    // Developer mode utilities
    fun getSimulationStatus(): Map<String, Any> {
        return mapOf(
            "isRunning" to isRunning,
            "compressionFactor" to compressionFactor,
            "currentScenario" to currentScenario.name,
            "baselineHeartRate" to baselineHeartRate,
            "baselineSpO2" to baselineSpO2,
            "baselineStressScore" to baselineStressScore,
            "baselineStepsPerHour" to baselineStepsPerHour,
            "baselineSleepHours" to baselineSleepHours
        )
    }

    fun forceVitalsSample(vitals: VitalsSample) {
        // For testing: immediately emit a specific vitals sample
        kotlinx.coroutines.GlobalScope.launch {
            _vitalsFlow.emit(vitals)
        }
    }

    fun getCurrentCompressionDescription(): String {
        return when (compressionFactor) {
            1 -> "Real-time (15 min intervals)"
            30 -> "Fast demo (30 sec intervals)"
            60 -> "Very fast demo (10 sec intervals)"
            300 -> "Ultra-fast demo (2 sec intervals)"
            else -> "Custom (${compressionFactor}x speed)"
        }
    }
}