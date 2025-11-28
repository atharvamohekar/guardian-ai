package com.guardianai.app.data.database.dao

import androidx.room.*
import com.guardianai.app.data.database.entities.PredictionIncidentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PredictionIncidentDao {

    @Query("SELECT * FROM prediction_incidents WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllIncidents(userId: Int): Flow<List<PredictionIncidentEntity>>

    @Query("SELECT * FROM prediction_incidents WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentIncidents(userId: Int, limit: Int = 10): Flow<List<PredictionIncidentEntity>>

    @Query("SELECT * FROM prediction_incidents WHERE id = :id")
    suspend fun getIncidentById(id: String): PredictionIncidentEntity?

    @Query("SELECT * FROM prediction_incidents WHERE userId = :userId AND resolved = 0 ORDER BY timestamp DESC")
    suspend fun getUnresolvedIncidents(userId: Int): List<PredictionIncidentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIncident(incident: PredictionIncidentEntity)

    @Update
    suspend fun updateIncident(incident: PredictionIncidentEntity)

    @Query("UPDATE prediction_incidents SET resolved = 1, resolvedAt = :resolvedAt WHERE id = :incidentId")
    suspend fun markIncidentResolved(incidentId: String, resolvedAt: Long = System.currentTimeMillis())

    @Query("UPDATE prediction_incidents SET escalated = 1 WHERE id = :incidentId")
    suspend fun markIncidentEscalated(incidentId: String)

    @Query("UPDATE prediction_incidents SET emergencyWorkflowTriggered = 1 WHERE id = :incidentId")
    suspend fun markEmergencyWorkflowTriggered(incidentId: String)

    @Delete
    suspend fun deleteIncident(incident: PredictionIncidentEntity)

    @Query("DELETE FROM prediction_incidents WHERE userId = :userId AND resolved = 1 AND resolvedAt < :cutoffDate")
    suspend fun deleteOldResolvedIncidents(userId: Int, cutoffDate: Long)

    @Query("SELECT COUNT(*) FROM prediction_incidents WHERE userId = :userId AND timestamp >= :since")
    suspend fun getIncidentCountSince(userId: Int, since: Long): Int

    @Query("SELECT * FROM prediction_incidents WHERE userId = :userId AND severity = :severity ORDER BY timestamp DESC LIMIT :limit")
    fun getIncidentsBySeverity(userId: Int, severity: String, limit: Int = 5): Flow<List<PredictionIncidentEntity>>
}