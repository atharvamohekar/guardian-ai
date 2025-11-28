package com.guardianai.app.domain.models

import androidx.room.Entity
import androidx.room.PrimaryKey

data class UserProfile(
    val id: Int = 0,
    val fullName: String,
    val age: Int,
    val gender: Gender,
    val heightCm: Int,
    val weightKg: Int,
    val lifestyle: LifestyleProfile,
    val medicalProfile: MedicalProfile,
    val emergencyContact: EmergencyContact,
    val primaryDoctor: PrimaryDoctor,
    val autonomyLevel: AutonomyLevel,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class LifestyleProfile(
    val smoking: Boolean,
    val alcohol: Boolean,
    val proteinIntake: ProteinIntake,
    val leafyVegetableFrequency: VegetableFrequency,
    val dailyWaterIntake: Float,
    val weeklyExerciseFrequency: ExerciseFrequency,
    val averageSleepHours: Float
)

data class MedicalProfile(
    val knownConditions: List<MedicalCondition>,
    val otherConditions: String = "",
    val currentMedications: String = ""
)

data class EmergencyContact(
    val name: String,
    val phone: String
)

data class PrimaryDoctor(
    val name: String = "",
    val phone: String = ""
)

enum class Gender {
    MALE, FEMALE, OTHER
}

enum class ProteinIntake {
    YES, NO, OCCASIONALLY
}

enum class VegetableFrequency {
    DAILY, FOUR_TO_FIVE_WEEKLY, TWO_TO_THREE_WEEKLY, RARELY
}

enum class ExerciseFrequency {
    NONE, ONE_TO_TWO_WEEKLY, THREE_TO_FOUR_WEEKLY, FIVE_PLUS_WEEKLY
}

enum class MedicalCondition {
    DIABETES, HYPERTENSION, ASTHMA, HEART_DISEASE, ARTHRITIS, ALLERGIES, THYROID_DISORDER
}

enum class AutonomyLevel {
    SEMI_AUTOMATIC, FULLY_AUTOMATIC
}