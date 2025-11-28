package com.guardianai.app.data.repository

import com.guardianai.app.data.database.dao.UserProfileDao
import com.guardianai.app.data.database.entities.UserProfileEntity
import com.guardianai.app.data.datastore.PreferencesDataStore
import com.guardianai.app.domain.models.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepository @Inject constructor(
    private val userProfileDao: UserProfileDao,
    private val preferencesDataStore: PreferencesDataStore,
    private val gson: Gson
) {

    // Get user profile as Flow
    fun getUserProfile(): Flow<UserProfile?> {
        return userProfileDao.getUserProfile().map { entity ->
            entity?.let { mapToDomainModel(it) }
        }
    }

    // Get user profile synchronously
    suspend fun getUserProfileSync(): UserProfile? {
        val entity = userProfileDao.getUserProfileSync()
        return entity?.let { mapToDomainModel(it) }
    }

    // Save user profile
    suspend fun saveUserProfile(userProfile: UserProfile) {
        val entity = mapToEntity(userProfile)
        userProfileDao.insertUserProfile(entity)
    }

    // Update user profile
    suspend fun updateUserProfile(userProfile: UserProfile) {
        val entity = mapToEntity(userProfile)
        userProfileDao.updateUserProfile(entity)
    }

    // Check if onboarding is complete
    fun isOnboardingComplete(): Flow<Boolean> {
        return preferencesDataStore.onboardingComplete
    }

    // Set onboarding complete
    suspend fun setOnboardingComplete() {
        preferencesDataStore.setOnboardingComplete(true)

        // Also update in database
        val existingProfile = getUserProfileSync()
        existingProfile?.let { profile ->
            val updatedProfile = profile.copy(onboardingCompleted = true)
            updateUserProfile(updatedProfile)
        }
    }

    // Get autonomy mode
    fun getAutonomyMode(): Flow<AutonomyLevel> {
        return preferencesDataStore.autonomyMode.map { modeString ->
            when (modeString) {
                "fully_automatic" -> AutonomyLevel.FULLY_AUTOMATIC
                else -> AutonomyLevel.SEMI_AUTOMATIC
            }
        }
    }

    // Set autonomy mode
    suspend fun setAutonomyMode(mode: AutonomyLevel) {
        val modeString = when (mode) {
            AutonomyLevel.FULLY_AUTOMATIC -> "fully_automatic"
            AutonomyLevel.SEMI_AUTOMATIC -> "semi_automatic"
        }
        preferencesDataStore.setAutonomyMode(modeString)
    }

    // Check if user exists
    suspend fun userExists(): Boolean {
        return userProfileDao.getProfileCount() > 0
    }

    // Clear all user data
    suspend fun clearUserData() {
        userProfileDao.clearAllProfiles()
        preferencesDataStore.clearAllPreferences()
    }

    // Get emergency contact info
    suspend fun getEmergencyContact(): EmergencyContact? {
        val profile = getUserProfileSync()
        return profile?.emergencyContact
    }

    // Update emergency contact
    suspend fun updateEmergencyContact(contact: EmergencyContact) {
        val profile = getUserProfileSync()
        profile?.let { currentProfile ->
            val updatedProfile = currentProfile.copy(emergencyContact = contact)
            updateUserProfile(updatedProfile)
        }
    }

    // Get primary doctor info
    suspend fun getPrimaryDoctor(): PrimaryDoctor {
        val profile = getUserProfileSync()
        return profile?.primaryDoctor ?: PrimaryDoctor()
    }

    // Update primary doctor
    suspend fun updatePrimaryDoctor(doctor: PrimaryDoctor) {
        val profile = getUserProfileSync()
        profile?.let { currentProfile ->
            val updatedProfile = currentProfile.copy(primaryDoctor = doctor)
            updateUserProfile(updatedProfile)
        }
    }

    // Get medical profile
    suspend fun getMedicalProfile(): MedicalProfile? {
        val profile = getUserProfileSync()
        return profile?.medicalProfile
    }

    // Get lifestyle profile
    suspend fun getLifestyleProfile(): LifestyleProfile? {
        val profile = getUserProfileSync()
        return profile?.lifestyle
    }

    // Get basic info
    suspend fun getBasicInfo(): Triple<String, Int, Gender>? {
        val profile = getUserProfileSync()
        return profile?.let { Triple(it.fullName, it.age, it.gender) }
    }

    // Profile statistics for developer mode
    suspend fun getProfileStats(): Map<String, Any> {
        val profile = getUserProfileSync()
        return mapOf(
            "profile_exists" to (profile != null),
            "onboarding_complete" to (preferencesDataStore.onboardingComplete.first()),
            "autonomy_mode" to (preferencesDataStore.autonomyMode.first()),
            "has_emergency_contact" to (profile?.emergencyContact?.name?.isNotEmpty() == true),
            "has_primary_doctor" to (profile?.primaryDoctor?.name?.isNotEmpty() == true),
            "medical_conditions_count" to (profile?.medicalProfile?.knownConditions?.size ?: 0),
            "age" to (profile?.age ?: 0),
            "gender" to (profile?.gender?.name ?: "unknown")
        )
    }

    // Private mapping functions
    private fun mapToDomainModel(entity: UserProfileEntity): UserProfile {
        return UserProfile(
            id = entity.id,
            fullName = entity.fullName,
            age = entity.age,
            gender = when (entity.gender) {
                "MALE" -> Gender.MALE
                "FEMALE" -> Gender.FEMALE
                else -> Gender.OTHER
            },
            heightCm = entity.heightCm,
            weightKg = entity.weightKg,
            lifestyle = gson.fromJson(entity.lifestyleJson, LifestyleProfile::class.java),
            medicalProfile = gson.fromJson(entity.medicalProfileJson, MedicalProfile::class.java),
            emergencyContact = gson.fromJson(entity.emergencyContactJson, EmergencyContact::class.java),
            primaryDoctor = gson.fromJson(entity.primaryDoctorJson, PrimaryDoctor::class.java),
            autonomyLevel = when (entity.autonomyLevel) {
                "fully_automatic" -> AutonomyLevel.FULLY_AUTOMATIC
                else -> AutonomyLevel.SEMI_AUTOMATIC
            },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    private fun mapToEntity(domainModel: UserProfile): UserProfileEntity {
        return UserProfileEntity(
            id = domainModel.id,
            fullName = domainModel.fullName,
            age = domainModel.age,
            gender = when (domainModel.gender) {
                Gender.MALE -> "MALE"
                Gender.FEMALE -> "FEMALE"
                Gender.OTHER -> "OTHER"
            },
            heightCm = domainModel.heightCm,
            weightKg = domainModel.weightKg,
            lifestyleJson = gson.toJson(domainModel.lifestyle),
            medicalProfileJson = gson.toJson(domainModel.medicalProfile),
            emergencyContactJson = gson.toJson(domainModel.emergencyContact),
            primaryDoctorJson = gson.toJson(domainModel.primaryDoctor),
            autonomyLevel = when (domainModel.autonomyLevel) {
                AutonomyLevel.FULLY_AUTOMATIC -> "fully_automatic"
                AutonomyLevel.SEMI_AUTOMATIC -> "semi_automatic"
            },
            createdAt = domainModel.createdAt,
            updatedAt = System.currentTimeMillis(),
            onboardingCompleted = false // Will be updated separately
        )
    }
}