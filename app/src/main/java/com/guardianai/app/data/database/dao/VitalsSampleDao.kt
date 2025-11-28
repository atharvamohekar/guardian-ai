package com.guardianai.app.data.database.dao

import androidx.room.*
import com.guardianai.app.data.database.entities.VitalsSampleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalsSampleDao {

    @Query("SELECT * FROM vitals_samples WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getLatestVitals(userId: Int, limit: Int = 10): Flow<List<VitalsSampleEntity>>

    @Query("SELECT * FROM vitals_samples WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestVitalSample(userId: Int): VitalsSampleEntity?

    @Query("SELECT * FROM vitals_samples WHERE userId = :userId AND timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp ASC")
    suspend fun getVitalsInRange(userId: Int, startTime: Long, endTime: Long): List<VitalsSampleEntity>

    @Query("SELECT * FROM vitals_samples WHERE userId = :userId AND isAnomaly = 1 ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentAnomalies(userId: Int, limit: Int = 5): Flow<List<VitalsSampleEntity>>

    @Query("SELECT * FROM vitals_samples WHERE userId = :userId AND isAnomaly = 1 AND timestamp >= :since ORDER BY timestamp ASC")
    suspend fun getAnomaliesSince(userId: Int, since: Long): List<VitalsSampleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitalsSample(vitalsSample: VitalsSampleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVitalsSamples(vitalsSamples: List<VitalsSampleEntity>)

    @Update
    suspend fun updateVitalsSample(vitalsSample: VitalsSampleEntity)

    @Query("DELETE FROM vitals_samples WHERE userId = :userId AND timestamp < :cutoffDate")
    suspend fun deleteOldVitals(userId: Int, cutoffDate: Long)

    @Query("DELETE FROM vitals_samples WHERE userId = :userId")
    suspend fun deleteUserVitals(userId: Int)

    @Query("SELECT COUNT(*) FROM vitals_samples WHERE userId = :userId")
    suspend fun getVitalsCount(userId: Int): Int

    @Query("SELECT AVG(heartRate) FROM vitals_samples WHERE userId = :userId AND timestamp >= :since")
    suspend fun getAverageHeartRateSince(userId: Int, since: Long): Float?

    @Query("SELECT AVG(spO2) FROM vitals_samples WHERE userId = :userId AND timestamp >= :since")
    suspend fun getAverageSpO2Since(userId: Int, since: Long): Float?

    @Query("SELECT AVG(stressScore) FROM vitals_samples WHERE userId = :userId AND timestamp >= :since")
    suspend fun getAverageStressScoreSince(userId: Int, since: Long): Float?
}