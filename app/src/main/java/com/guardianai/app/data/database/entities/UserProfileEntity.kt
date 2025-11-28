package com.guardianai.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.guardianai.app.data.database.converters.Converters

@Entity(tableName = "user_profiles")
@TypeConverters(Converters::class)
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,
    val age: Int,
    val gender: String, // "MALE", "FEMALE", "OTHER"
    val heightCm: Int,
    val weightKg: Int,
    val lifestyleJson: String, // JSON serialized LifestyleProfile
    val medicalProfileJson: String, // JSON serialized MedicalProfile
    val emergencyContactJson: String, // JSON serialized EmergencyContact
    val primaryDoctorJson: String, // JSON serialized PrimaryDoctor
    val autonomyLevel: String, // "SEMI_AUTOMATIC", "FULLY_AUTOMATIC"
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val onboardingCompleted: Boolean = false
)