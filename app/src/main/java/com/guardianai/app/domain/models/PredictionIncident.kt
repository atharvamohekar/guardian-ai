package com.guardianai.app.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class PredictionIncident(
    @PrimaryKey val id: String,
    val userId: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val incidentType: IncidentType,
    val severity: Severity,
    val detectedMetrics: List<DetectedMetric>,
    val explanation: String,
    val recommendations: List<String>,
    val verificationReadings: List<VitalsSample>,
    val resolved: Boolean = false,
    val resolvedAt: Long? = null,
    val escalated: Boolean = false,
    val emergencyWorkflowTriggered: Boolean = false
)

data class DetectedMetric(
    val metricType: MetricType,
    val currentValue: Float,
    val thresholdValue: Float,
    val deviationPercentage: Float,
    val trendDirection: TrendDirection
)

enum class IncidentType {
    ELEVATED_HEART_RATE, LOW_SPO2, HIGH_STRESS_LEVEL, SLEEP_DEPRIVATION, IRREGULAR_PATTERN, COMBINED_ANOMALY
}

enum class Severity {
    LOW, MODERATE, HIGH, CRITICAL
}

enum class MetricType {
    HEART_RATE, SPO2, STRESS_SCORE, SLEEP_HOURS, STEPS
}

enum class TrendDirection {
    INCREASING, DECREASING, STABLE, VOLATILE
}