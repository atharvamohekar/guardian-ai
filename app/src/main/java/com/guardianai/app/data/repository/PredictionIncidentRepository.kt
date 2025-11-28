package com.guardianai.app.data.repository

import com.guardianai.app.data.database.dao.PredictionIncidentDao
import com.guardianai.app.data.database.entities.PredictionIncidentEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictionIncidentRepository @Inject constructor(
    private val predictionDao: PredictionIncidentDao,
    private val gson: Gson
) {

    fun getAllIncidents(): Flow<List<PredictionIncident>> {
        return predictionDao.getAllIncidents().map { entities ->
            entities.map { mapToDomain(it) }
        }
    }

    fun getRecentIncidents(limit: Int = 10): Flow<List<PredictionIncident>> {
        return predictionDao.getRecentIncidents(limit).map { entities ->
            entities.map { mapToDomain(it) }
        }
    }

    suspend fun getIncidentById(id: String): PredictionIncident? {
        val entity = predictionDao.getIncidentById(id)
        return entity?.let { mapToDomain(it) }
    }

    suspend fun insertIncident(incident: PredictionIncident) {
        val entity = mapToEntity(incident)
        predictionDao.insertIncident(entity)
    }

    suspend fun markIncidentResolved(incidentId: String) {
        predictionDao.markIncidentResolved(incidentId, System.currentTimeMillis())
    }

    suspend fun markIncidentEscalated(incidentId: String) {
        predictionDao.markIncidentEscalated(incidentId)
    }

    suspend fun markEmergencyWorkflowTriggered(incidentId: String) {
        predictionDao.markEmergencyWorkflowTriggered(incidentId)
    }

    suspend fun deleteIncident(incident: PredictionIncident) {
        val entity = mapToEntity(incident)
        predictionDao.deleteIncident(entity)
    }

    private fun mapToDomain(entity: PredictionIncidentEntity): PredictionIncident {
        return PredictionIncident(
            id = entity.id,
            userId = entity.userId,
            timestamp = entity.timestamp,
            incidentType = when (entity.incidentType) {
                "ELEVATED_HEART_RATE" -> com.guardianai.app.domain.models.IncidentType.ELEVATED_HEART_RATE
                "LOW_SPO2" -> com.guardianai.app.domain.models.IncidentType.LOW_SPO2
                "HIGH_STRESS_LEVEL" -> com.guardianai.app.domain.models.IncidentType.HIGH_STRESS_LEVEL
                "SLEEP_DEPRIVATION" -> com.guardianai.app.domain.models.IncidentType.SLEEP_DEPRIVATION
                "IRREGULAR_PATTERN" -> com.guardianai.app.domain.models.IncidentType.IRREGULAR_PATTERN
                "COMBINED_ANOMALY" -> com.guardianai.app.domain.models.IncidentType.COMBINED_ANOMALY
                else -> com.guardianai.app.domain.models.IncidentType.ELEVATED_HEART_RATE
            },
            severity = when (entity.severity) {
                "LOW" -> com.guardianai.app.domain.models.Severity.LOW
                "MODERATE" -> com.guardianai.app.domain.models.Severity.MODERATE
                "ELEVATED" -> com.guardianai.app.domain.models.Severity.ELEVATED
                "HIGH" -> com.guardianai.app.domain.models.Severity.HIGH
                "CRITICAL" -> com.guardianai.app.domain.models.Severity.CRITICAL
                else -> com.guardianai.app.domain.models.Severity.MODERATE
            },
            detectedMetrics = try {
                gson.fromJson(entity.detectedMetricsJson, object : com.google.gson.reflect.TypeToken<List<DetectedMetric>>() {}.type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            explanation = entity.explanation,
            recommendations = try {
                gson.fromJson(entity.recommendationsJson, object : com.google.gson.reflect.TypeToken<List<String>>() {}.type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            verificationReadings = try {
                gson.fromJson(entity.verificationReadingsJson, object : com.google.gson.reflect.TypeToken<List<VitalsSample>>() {}.type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            resolved = entity.resolved,
            resolvedAt = entity.resolvedAt,
            escalated = entity.escalated,
            emergencyWorkflowTriggered = entity.emergencyWorkflowTriggered
        )
    }

    private fun mapToEntity(domainModel: PredictionIncident): PredictionIncidentEntity {
        return PredictionIncidentEntity(
            id = domainModel.id,
            userId = domainModel.userId,
            timestamp = domainModel.timestamp,
            incidentType = when (domainModel.incidentType) {
                com.guardianai.app.domain.models.IncidentType.ELEVATED_HEART_RATE -> "ELEVATED_HEART_RATE"
                com.guardianai.app.domain.models.IncidentType.LOW_SPO2 -> "LOW_SPO2"
                com.guardianai.app.domain.models.IncidentType.HIGH_STRESS_LEVEL -> "HIGH_STRESS_LEVEL"
                com.guardianai.app.domain.models.IncidentType.SLEEP_DEPRIVATION -> "SLEEP_DEPRIVATION"
                com.guardianai.app.domain.models.IncidentType.IRREGULAR_PATTERN -> "IRREGULAR_PATTERN"
                com.guardianai.app.domain.models.IncidentType.COMBINED_ANOMALY -> "COMBINED_ANOMALY"
                else -> "ELEVATED_HEART_RATE"
            },
            severity = when (domainModel.severity) {
                com.guardianai.app.domain.models.Severity.LOW -> "LOW"
                com.guardianai.app.domain.models.Severity.MODERATE -> "MODERATE"
                com.guardianai.app.domain.models.Severity.ELEVATED -> "ELEVATED"
                com.guardianai.app.domain.models.Severity.HIGH -> "HIGH"
                com.guardianai.app.domain.models.Severity.CRITICAL -> "CRITICAL"
                else -> "MODERATE"
            },
            detectedMetricsJson = gson.toJson(domainModel.detectedMetrics),
            explanation = domainModel.explanation,
            recommendationsJson = gson.toJson(domainModel.recommendations),
            verificationReadingsJson = gson.toJson(domainModel.verificationReadings),
            resolved = domainModel.resolved,
            resolvedAt = domainModel.resolvedAt,
            escalated = domainModel.escalated,
            emergencyWorkflowTriggered = domainModel.emergencyWorkflowTriggered
        )
    }
}