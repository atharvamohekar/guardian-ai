package com.guardianai.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import com.guardianai.app.data.datastore.PreferencesDataStore
import com.guardianai.app.data.repository.UserProfileRepository
import com.guardianai.app.data.repository.VitalsRepository
import com.guardianai.app.data.repository.PredictionIncidentRepository
import com.guardianai.app.data.repository.EmergencyLogRepository
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<androidx.datastore.preferences.core.Preferences> {
        return context.preferencesDataStore(name = "guardian_ai_preferences")
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideUserProfileRepository(
        userProfileDao: com.guardianai.app.data.database.dao.UserProfileDao,
        preferencesDataStore: PreferencesDataStore,
        gson: Gson
    ): UserProfileRepository {
        return UserProfileRepository(userProfileDao, preferencesDataStore, gson)
    }

    @Provides
    @Singleton
    fun provideVitalsRepository(
        vitalsDao: com.guardianai.app.data.database.dao.VitalsSampleDao
    ): VitalsRepository {
        return VitalsRepository(vitalsDao)
    }

    @Provides
    @Singleton
    fun providePredictionIncidentRepository(
        predictionDao: com.guardianai.app.data.database.dao.PredictionIncidentDao
    ): PredictionIncidentRepository {
        return PredictionIncidentRepository(predictionDao)
    }

    @Provides
    @Singleton
    fun provideEmergencyLogRepository(
        emergencyDao: com.guardianai.app.data.database.dao.EmergencyLogDao
    ): EmergencyLogRepository {
        return EmergencyLogRepository(emergencyDao)
    }
}