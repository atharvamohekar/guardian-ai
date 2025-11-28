package com.guardianai.app.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class EmergencyLog(
    @PrimaryKey val id: String,
    val userId: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val incidentId: String,
    val actions: List<EmergencyAction>,
    val contactsNotified: List<EmergencyContact>,
    val emergencyServicesCalled: Boolean,
    val location: LocationData,
    val responseTime: Long? = null,
    val outcome: EmergencyOutcome? = null,
    val notes: String = ""
)

data class EmergencyAction(
    val actionType: ActionType,
    val timestamp: Long = System.currentTimeMillis(),
    val success: Boolean,
    val errorMessage: String? = null,
    val additionalData: Map<String, String> = emptyMap()
)

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long = System.currentTimeMillis(),
    val provider: String = "unknown",
    val nearestHospital: Hospital? = null
)

data class Hospital(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Float,
    val emergencyServices: Boolean = true,
    val estimatedArrivalTime: Int? = null // in minutes
)

enum class ActionType {
    NOTIFY_EMERGENCY_CONTACT,
    CALL_EMERGENCY_SERVICES,
    NOTIFY_HOSPITAL,
    CALL_AMULANCE,
    GET_CURRENT_LOCATION,
    FIND_NEAREST_HOSPITAL,
    RECORD_VOICE_NOTE,
    SEND_SMS_ALERT,
    TRIGGER_ALARM_SOUND,
    VIBRATION_PATTERN
}

enum class EmergencyOutcome {
    CONTACTED_EMERGENCY_SERVICES,
    USER_CANCELLED,
    FALSE_ALARM,
    MEDICAL_ATTENTION_PROVIDED,
    TRANSPORT_TO_HOSPITAL,
    ADMITTED_TO_HOSPITAL,
    RESOLVED_ON_SITE
}