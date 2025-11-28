package com.guardianai.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "emergency_logs",
    indices = [Index(value = ["userId", "timestamp"]), Index(value = ["incidentId"])]
)
data class EmergencyLogEntity(
    @PrimaryKey
    val id: String,
    val userId: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val incidentId: String,
    val actionsJson: String, // JSON array of EmergencyAction
    val contactsNotifiedJson: String, // JSON array of EmergencyContact
    val emergencyServicesCalled: Boolean,
    val locationJson: String, // JSON serialized LocationData
    val responseTime: Long? = null,
    val outcome: String? = null, // "CONTACTED_EMERGENCY_SERVICES", etc.
    val notes: String = ""
)