package com.guardianai.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "prediction_incidents",
    indices = [Index(value = ["userId", "timestamp"]), Index(value = ["resolved"])]
)
data class PredictionIncidentEntity(
    @PrimaryKey
    val id: String,
    val userId: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val incidentType: String, // "ELEVATED_HEART_RATE", "LOW_SPO2", etc.
    val severity: String, // "LOW", "MODERATE", "HIGH", "CRITICAL"
    val detectedMetricsJson: String, // JSON array of DetectedMetric
    val explanation: String,
    val recommendationsJson: String, // JSON array of recommendation strings
    val verificationReadingsJson: String, // JSON array of vitals sample IDs
    val resolved: Boolean = false,
    val resolvedAt: Long? = null,
    val escalated: Boolean = false,
    val emergencyWorkflowTriggered: Boolean = false
)