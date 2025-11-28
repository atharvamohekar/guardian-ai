package com.guardianai.app.data.repository

import com.guardianai.app.data.database.dao.VitalsSampleDao
import com.guardianai.app.data.database.entities.VitalsSampleEntity
import com.guardianai.app.domain.models.VitalsSample
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VitalsRepository @Inject constructor(
    private val vitalsDao: VitalsSampleDao
) {

    fun getLatestVitals(): Flow<VitalsSample?> {
        return vitalsDao.getLatestVitalSample().map { entity ->
            entity?.let { mapToDomain(it) }
        }
    }

    suspend fun getLatestVitalsSync(): VitalsSample? {
        val entity = vitalsDao.getLatestVitalSample()
        return entity?.let { mapToDomain(it) }
    }

    fun getVitalsInRange(startTime: Long, endTime: Long): Flow<List<VitalsSample>> {
        return vitalsDao.getVitalsInRange(startTime, endTime).map { entities ->
            entities.map { mapToDomain(it) }
        }
    }

    suspend fun insertVitalsSample(vitalsSample: VitalsSample) {
        val entity = mapToEntity(vitalsSample)
        vitalsDao.insertVitalsSample(entity)
    }

    suspend fun insertVitalsSamples(vitalsSamples: List<VitalsSample>) {
        val entities = vitalsSamples.map { mapToEntity(it) }
        vitalsDao.insertVitalsSamples(entities)
    }

    fun getRecentAnomalies(limit: Int): Flow<List<VitalsSample>> {
        return vitalsDao.getRecentAnomalies(limit).map { entities ->
            entities.map { mapToDomain(it) }
        }
    }

    suspend fun deleteOldVitals(userId: Int, cutoffDate: Long) {
        vitalsDao.deleteOldVitals(userId, cutoffDate)
    }

    private fun mapToDomain(entity: VitalsSampleEntity): VitalsSample {
        return VitalsSample(
            id = entity.id,
            userId = entity.userId,
            timestamp = entity.timestamp,
            heartRate = entity.heartRate,
            spO2 = entity.spO2,
            stressScore = entity.stressScore,
            steps = entity.steps,
            sleepHours = entity.sleepHours,
            dataSource = when (entity.dataSource) {
                "SIMULATED" -> com.guardianai.app.domain.models.DataSource.SIMULATED
                "REAL_WEARABLE" -> com.guardianai.app.domain.models.DataSource.REAL_WEARABLE
                "MANUAL_ENTRY" -> com.guardianai.app.domain.models.DataSource.MANUAL_ENTRY
                else -> com.guardianai.app.domain.models.DataSource.SIMULATED
            },
            isAnomaly = entity.isAnomaly,
            anomalyType = entity.anomalyType?.let { type ->
                when (type) {
                    "ELEVATED_HEART_RATE" -> com.guardianai.app.domain.models.AnomalyType.ELEVATED_HEART_RATE
                    "LOW_SPO2" -> com.guardianai.app.domain.models.AnomalyType.LOW_SPO2
                    "HIGH_STRESS" -> com.guardianai.app.domain.models.AnomalyType.HIGH_STRESS
                    "IRREGULAR_PATTERN" -> com.guardianai.app.domain.models.AnomalyType.IRREGULAR_PATTERN
                    "SLEEP_DEPRIVATION" -> com.guardianai.app.domain.models.AnomalyType.SLEEP_DEPRIVATION
                    else -> null
                }
            }
        )
    }

    private fun mapToEntity(domainModel: VitalsSample): VitalsSampleEntity {
        return VitalsSampleEntity(
            id = domainModel.id,
            userId = domainModel.userId,
            timestamp = domainModel.timestamp,
            heartRate = domainModel.heartRate,
            spO2 = domainModel.spO2,
            stressScore = domainModel.stressScore,
            steps = domainModel.steps,
            sleepHours = domainModel.sleepHours,
            dataSource = when (domainModel.dataSource) {
                com.guardianai.app.domain.models.DataSource.SIMULATED -> "SIMULATED"
                com.guardianai.app.domain.models.DataSource.REAL_WEARABLE -> "REAL_WEARABLE"
                com.guardianai.app.domain.models.DataSource.MANUAL_ENTRY -> "MANUAL_ENTRY"
            },
            isAnomaly = domainModel.isAnomaly,
            anomalyType = domainModel.anomalyType?.let { type ->
                when (type) {
                    com.guardianai.app.domain.models.AnomalyType.ELEVATED_HEART_RATE -> "ELEVATED_HEART_RATE"
                    com.guardianai.app.domain.models.AnomalyType.LOW_SPO2 -> "LOW_SPO2"
                    com.guardianai.app.domain.models.AnomalyType.HIGH_STRESS -> "HIGH_STRESS"
                    com.guardianai.app.domain.models.AnomalyType.IRREGULAR_PATTERN -> "IRREGULAR_PATTERN"
                    com.guardianai.app.domain.models.AnomalyType.SLEEP_DEPRIVATION -> "SLEEP_DEPRIVATION"
                }
            },
            verificationStep = null
        )
    }
}