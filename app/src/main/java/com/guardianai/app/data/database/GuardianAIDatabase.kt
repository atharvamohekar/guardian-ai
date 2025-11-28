package com.guardianai.app.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context
import com.guardianai.app.data.database.converters.Converters
import com.guardianai.app.data.database.dao.*
import com.guardianai.app.data.database.entities.*

@Database(
    entities = [
        UserProfileEntity::class,
        VitalsSampleEntity::class,
        PredictionIncidentEntity::class,
        EmergencyLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GuardianAIDatabase : RoomDatabase() {

    abstract fun userProfileDao(): UserProfileDao
    abstract fun vitalsSampleDao(): VitalsSampleDao
    abstract fun predictionIncidentDao(): PredictionIncidentDao
    abstract fun emergencyLogDao(): EmergencyLogDao

    companion object {
        const val DATABASE_NAME = "guardian_ai_database"

        // For future migrations
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Future migrations will be added here
                // For now, this is a placeholder
            }
        }
    }
}