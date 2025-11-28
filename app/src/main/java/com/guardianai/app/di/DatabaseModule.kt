package com.guardianai.app.di

import android.content.Context
import androidx.room.Room
import com.guardianai.app.data.database.GuardianAIDatabase
import com.guardianai.app.data.database.dao.EmergencyLogDao
import com.guardianai.app.data.database.dao.PredictionIncidentDao
import com.guardianai.app.data.database.dao.UserProfileDao
import com.guardianai.app.data.database.dao.VitalsSampleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): GuardianAIDatabase {
        return Room.databaseBuilder(
            context,
            GuardianAIDatabase::class.java,
            GuardianAIDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration() // For development - remove in production
        .build()
    }

    @Provides
    fun provideUserProfileDao(database: GuardianAIDatabase): UserProfileDao {
        return database.userProfileDao()
    }

    @Provides
    fun provideVitalsSampleDao(database: GuardianAIDatabase): VitalsSampleDao {
        return database.vitalsSampleDao()
    }

    @Provides
    fun providePredictionIncidentDao(database: GuardianAIDatabase): PredictionIncidentDao {
        return database.predictionIncidentDao()
    }

    @Provides
    fun provideEmergencyLogDao(database: GuardianAIDatabase): EmergencyLogDao {
        return database.emergencyLogDao()
    }
}