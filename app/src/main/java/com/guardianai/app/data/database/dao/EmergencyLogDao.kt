package com.guardianai.app.data.database.dao

import androidx.room.*
import com.guardianai.app.data.database.entities.EmergencyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmergencyLogDao {

    @Query("SELECT * FROM emergency_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllEmergencyLogs(userId: Int): Flow<List<EmergencyLogEntity>>

    @Query("SELECT * FROM emergency_logs WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEmergencyLogs(userId: Int, limit: Int = 10): Flow<List<EmergencyLogEntity>>

    @Query("SELECT * FROM emergency_logs WHERE id = :id")
    suspend fun getEmergencyLogById(id: String): EmergencyLogEntity?

    @Query("SELECT * FROM emergency_logs WHERE incidentId = :incidentId")
    suspend fun getEmergencyLogsForIncident(incidentId: String): List<EmergencyLogEntity>

    @Query("SELECT * FROM emergency_logs WHERE userId = :userId AND timestamp >= :since ORDER BY timestamp ASC")
    suspend fun getEmergencyLogsSince(userId: Int, since: Long): List<EmergencyLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmergencyLog(emergencyLog: EmergencyLogEntity)

    @Update
    suspend fun updateEmergencyLog(emergencyLog: EmergencyLogEntity)

    @Query("UPDATE emergency_logs SET responseTime = :responseTime WHERE id = :logId")
    suspend fun updateResponseTime(logId: String, responseTime: Long)

    @Query("UPDATE emergency_logs SET outcome = :outcome WHERE id = :logId")
    suspend fun updateOutcome(logId: String, outcome: String)

    @Delete
    suspend fun deleteEmergencyLog(emergencyLog: EmergencyLogEntity)

    @Query("DELETE FROM emergency_logs WHERE userId = :userId AND timestamp < :cutoffDate")
    suspend fun deleteOldEmergencyLogs(userId: Int, cutoffDate: Long)

    @Query("SELECT COUNT(*) FROM emergency_logs WHERE userId = :userId AND timestamp >= :since")
    suspend fun getEmergencyLogCountSince(userId: Int, since: Long): Int

    @Query("SELECT COUNT(*) FROM emergency_logs WHERE userId = :userId AND emergencyServicesCalled = 1 AND timestamp >= :since")
    suspend fun getEmergencyServicesCallCountSince(userId: Int, since: Long): Int

    @Query("SELECT AVG(responseTime) FROM emergency_logs WHERE userId = :userId AND responseTime IS NOT NULL AND timestamp >= :since")
    suspend fun getAverageResponseTimeSince(userId: Int, since: Long): Long?

    @Query("SELECT * FROM emergency_logs WHERE userId = :userId AND emergencyServicesCalled = 1 ORDER BY timestamp DESC LIMIT :limit")
    fun getEmergencyServiceCalls(userId: Int, limit: Int = 5): Flow<List<EmergencyLogEntity>>
}