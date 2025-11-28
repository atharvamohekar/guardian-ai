package com.guardianai.app.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class VitalsSample(
    @PrimaryKey val id: String,
    val userId: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val heartRate: Int,
    val spO2: Int,
    val stressScore: Int,
    val steps: Int,
    val sleepHours: Float = 0f,
    val dataSource: DataSource,
    val isAnomaly: Boolean = false,
    val anomalyType: AnomalyType? = null
)

enum class DataSource {
    SIMULATED, REAL_WEARABLE, MANUAL_ENTRY
}

enum class AnomalyType {
    ELEVATED_HEART_RATE, LOW_SPO2, HIGH_STRESS, IRREGULAR_PATTERN, SLEEP_DEPRIVATION
}