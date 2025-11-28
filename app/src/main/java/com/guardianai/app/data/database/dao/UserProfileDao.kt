package com.guardianai.app.data.database.dao

import androidx.room.*
import com.guardianai.app.data.database.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profiles LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfileEntity): Long

    @Update
    suspend fun updateUserProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profiles SET onboardingCompleted = 1 WHERE id = :profileId")
    suspend fun setOnboardingCompleted(profileId: Int)

    @Query("SELECT onboardingCompleted FROM user_profiles LIMIT 1")
    suspend fun isOnboardingCompleted(): Boolean?

    @Query("DELETE FROM user_profiles")
    suspend fun clearAllProfiles()

    @Query("SELECT COUNT(*) FROM user_profiles")
    suspend fun getProfileCount(): Int
}