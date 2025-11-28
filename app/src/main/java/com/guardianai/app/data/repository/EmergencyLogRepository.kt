package com.guardianai.app.data.repository

import com.guardianai.app.data.database.dao.EmergencyLogDao
import com.guardianai.app.data.database.entities.EmergencyLogEntity
import com.guardianai.app.domain.models.*
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmergencyLogRepository @Inject constructor(
    private val emergencyDao: EmergencyLogDao,
    private val gson: Gson
) {

    fun getAllEmergencyLogs(): Flow<List<EmergencyLog>> {
        return emergencyDao.getAllEmergencyLogs().map { entities ->
            entities.map { mapToDomain(it) }
        }
    }

    fun getRecentEmergencyLogs(limit: Int = 10): Flow<List<EmergencyLog>> {
        return emergencyDao.getRecentEmergencyLogs(limit).map { entities ->
            entities.map { mapToDomain(it) }
        }
    }

    suspend fun getEmergencyLogById(id: String): EmergencyLog? {
        val entity = emergencyDao.getEmergencyLogById(id)
        return entity?.let { mapToDomain(it) }
    }

    suspend fun getEmergencyLogsForIncident(incidentId: String): List<EmergencyLog> {
        val entities = emergencyDao.getEmergencyLogsForIncident(incidentId)
        return entities.map { mapToDomain(it) }
    }

    suspend fun insertEmergencyLog(emergencyLog: EmergencyLog) {
        val entity = mapToEntity(emergencyLog)
        emergencyDao.insertEmergencyLog(entity)
    }

    suspend fun updateResponseTime(logId: String, responseTime: Long) {
        emergencyDao.updateResponseTime(logId, responseTime)
    }

    suspend fun updateOutcome(logId: String, outcome: EmergencyOutcome) {
        emergencyDao.updateOutcome(logId, outcome.name)
    }

    private fun mapToDomain(entity: EmergencyLogEntity): EmergencyLog {
        return EmergencyLog(
            id = entity.id,
            userId = entity.userId,
            timestamp = entity.timestamp,
            incidentId = entity.incidentId,
            actions = try {
                gson.fromJson(entity.actionsJson, object : com.google.gson.reflect.TypeToken<List<EmergencyAction>>() {}.type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            contactsNotified = try {
                gson.fromJson(entity.contactsNotifiedJson, object : com.google.gson.reflect.TypeToken<List<EmergencyContact>>() {}.type) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            },
            emergencyServicesCalled = entity.emergencyServicesCalled,
            location = try {
                gson.fromJson(entity.locationJson, object : com.google.gson.reflect.TypeToken<LocationData>>() {}.type) ?: LocationData(0.0, 0.0, 0f)
            } catch (e: Exception) {
                LocationData(0.0, 0.0, 0f)
            },
            responseTime = entity.responseTime,
            outcome = entity.outcome?.let { outcomeName ->
                when (outcomeName) {
                    "CONTACTED_EMERGENCY_SERVICES" -> EmergencyOutcome.CONTACTED_EMERGENCY_SERVICES
                    "USER_CANCELLED" -> EmergencyOutcome.USER_CANCELLED
                    "FALSE_ALARM" -> EmergencyOutcome.FALSE_ALARM
                    "MEDICAL_ATTENTION_PROVIDED" -> EmergencyOutcome.MEDICAL_ATTENTION_PROVIDED
                    "TRANSPORT_TO_HOSPITAL" -> EmergencyOutcome.TRANSPORT_TO_HOSPITAL
                    "ADMITTED_TO_HOSPITAL" -> EmergencyOutcome.ADMITTED_TO_HOSPITAL
                    "RESOLVED_ON_SITE" -> EmergencyOutcome.RESOLVED_ON_SITE
                    else -> EmergencyOutcome.USER_CANCELLED
                }
            },
            notes = entity.notes
        )
    }

    private fun mapToEntity(domainModel: EmergencyLog): EmergencyLogEntity {
        return EmergencyLogEntity(
            id = domainModel.id,
            userId = domainModel.userId,
            timestamp = domainModel.timestamp,
            incidentId = domainModel.incidentId,
            actionsJson = gson.toJson(domainModel.actions),
            contactsNotifiedJson = gson.toJson(domainModel.contactsNotified),
            emergencyServicesCalled = domainModel.emergencyServicesCalled,
            locationJson = gson.toJson(domainModel.location),
            responseTime = domainModel.responseTime,
            outcome = domainModel.outcome?.name,
            notes = domainModel.notes
        )
    }
}