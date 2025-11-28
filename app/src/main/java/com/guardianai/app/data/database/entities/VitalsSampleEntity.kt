package com.guardianai.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "vitals_samples",
    indices = [Index(value = ["userId", "timestamp"]), Index(value = ["isAnomaly"])]
)
data class VitalsSampleEntity(
    @PrimaryKey
    val id: String,
    val userId: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val heartRate: Int,
    val spO2: Int,
    val stressScore: Int,
    val steps: Int,
    val sleepHours: Float = 0f,
    val dataSource: String, // "SIMULATED", "REAL_WEARABLE", "MANUAL_ENTRY"
    val isAnomaly: Boolean = false,
    val anomalyType: String? = null, // "ELEVATED_HEART_RATE", "LOW_SPO2", etc.
    val verificationStep: Int? = null // 1, 2, or 3 for verification workflow
)