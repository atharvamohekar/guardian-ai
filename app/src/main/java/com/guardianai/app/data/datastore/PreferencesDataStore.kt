package com.guardianai.app.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "guardian_ai_preferences")

@Singleton
class PreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // Preference keys
    companion object PreferencesKeys {
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
        val AUTONOMY_MODE = stringPreferencesKey("autonomy_mode") // "semi_automatic" | "fully_automatic"
        val PAUSE_PREDICTIONS_UNTIL = longPreferencesKey("pause_predictions_until")
        val DEVELOPER_MODE = booleanPreferencesKey("developer_mode")
        val TIME_COMPRESSION_FACTOR = intPreferencesKey("time_compression_factor") // 1 = real time, 30 = compressed
        val LAST_VITALS_UPDATE = longPreferencesKey("last_vitals_update")
        val EMERGENCY_ENABLED = booleanPreferencesKey("emergency_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val NOTIFICATION_PERMISSION_GRANTED = booleanPreferencesKey("notification_permission_granted")
        val LOCATION_PERMISSION_GRANTED = booleanPreferencesKey("location_permission_granted")
        val PHONE_PERMISSION_GRANTED = booleanPreferencesKey("phone_permission_granted")
        val BACKGROUND_LOCATION_PERMISSION_GRANTED = booleanPreferencesKey("background_location_permission_granted")
        val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
        val APP_VERSION = stringPreferencesKey("app_version")
        val LAST_BACKUP_TIME = longPreferencesKey("last_backup_time")
        val AUTO_BACKUP_ENABLED = booleanPreferencesKey("auto_backup_enabled")
        val EMERGENCY_CONTACT_NOTIFIED_COUNT = intPreferencesKey("emergency_contact_notified_count")
        val LAST_EMERGENCY_SIMULATION = longPreferencesKey("last_emergency_simulation")
    }

    // Onboarding
    val onboardingComplete: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETE] ?: false
    }

    suspend fun setOnboardingComplete(complete: Boolean) {
        dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETE] = complete
        }
    }

    // Autonomy Mode
    val autonomyMode: Flow<String> = dataStore.data.map { preferences ->
        preferences[AUTONOMY_MODE] ?: "semi_automatic"
    }

    suspend fun setAutonomyMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[AUTONOMY_MODE] = mode
        }
    }

    // Pause Predictions
    val pausePredictionsUntil: Flow<Long> = dataStore.data.map { preferences ->
        preferences[PAUSE_PREDICTIONS_UNTIL] ?: 0L
    }

    suspend fun setPausePredictionsUntil(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[PAUSE_PREDICTIONS_UNTIL] = timestamp
        }
    }

    suspend fun clearPausePredictions() {
        dataStore.edit { preferences ->
            preferences.remove(PAUSE_PREDICTIONS_UNTIL)
        }
    }

    // Developer Mode
    val developerMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[DEVELOPER_MODE] ?: BuildConfig.DEBUG // Default based on build type
    }

    suspend fun setDeveloperMode(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[DEVELOPER_MODE] = enabled
        }
    }

    // Time Compression
    val timeCompressionFactor: Flow<Int> = dataStore.data.map { preferences ->
        preferences[TIME_COMPRESSION_FACTOR] ?: if (BuildConfig.DEBUG) 30 else 1 // Default: 30x in debug, 1x in release
    }

    suspend fun setTimeCompressionFactor(factor: Int) {
        dataStore.edit { preferences ->
            preferences[TIME_COMPRESSION_FACTOR] = factor
        }
    }

    // Last Vitals Update
    val lastVitalsUpdate: Flow<Long> = dataStore.data.map { preferences ->
        preferences[LAST_VITALS_UPDATE] ?: 0L
    }

    suspend fun setLastVitalsUpdate(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_VITALS_UPDATE] = timestamp
        }
    }

    // Emergency Settings
    val emergencyEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[EMERGENCY_ENABLED] ?: true
    }

    suspend fun setEmergencyEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[EMERGENCY_ENABLED] = enabled
        }
    }

    val soundEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[SOUND_ENABLED] ?: true
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    val vibrationEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
            preferences[VIBRATION_ENABLED] ?: true
        }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }

    // Permissions Tracking
    val notificationPermissionGranted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[NOTIFICATION_PERMISSION_GRANTED] ?: false
    }

    suspend fun setNotificationPermissionGranted(granted: Boolean) {
        dataStore.edit { preferences ->
            preferences[NOTIFICATION_PERMISSION_GRANTED] = granted
        }
    }

    val locationPermissionGranted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[LOCATION_PERMISSION_GRANTED] ?: false
    }

    suspend fun setLocationPermissionGranted(granted: Boolean) {
        dataStore.edit { preferences ->
            preferences[LOCATION_PERMISSION_GRANTED] = granted
        }
    }

    val phonePermissionGranted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[PHONE_PERMISSION_GRANTED] ?: false
    }

    suspend fun setPhonePermissionGranted(granted: Boolean) {
        dataStore.edit { preferences ->
            preferences[PHONE_PERMISSION_GRANTED] = granted
        }
    }

    val backgroundLocationPermissionGranted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[BACKGROUND_LOCATION_PERMISSION_GRANTED] ?: false
    }

    suspend fun setBackgroundLocationPermissionGranted(granted: Boolean) {
        dataStore.edit { preferences ->
            preferences[BACKGROUND_LOCATION_PERMISSION_GRANTED] = granted
        }
    }

    // First Launch
    val firstLaunch: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[FIRST_LAUNCH] ?: true
    }

    suspend fun setFirstLaunch(completed: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = completed
        }
    }

    // App Version
    val appVersion: Flow<String> = dataStore.data.map { preferences ->
        preferences[APP_VERSION] ?: ""
    }

    suspend fun setAppVersion(version: String) {
        dataStore.edit { preferences ->
            preferences[APP_VERSION] = version
        }
    }

    // Backup Settings
    val lastBackupTime: Flow<Long> = dataStore.data.map { preferences ->
        preferences[LAST_BACKUP_TIME] ?: 0L
    }

    suspend fun setLastBackupTime(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_BACKUP_TIME] = timestamp
        }
    }

    val autoBackupEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[AUTO_BACKUP_ENABLED] ?: false
    }

    suspend fun setAutoBackupEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[AUTO_BACKUP_ENABLED] = enabled
        }
    }

    // Emergency Statistics
    val emergencyContactNotifiedCount: Flow<Int> = dataStore.data.map { preferences ->
        preferences[EMERGENCY_CONTACT_NOTIFIED_COUNT] ?: 0
    }

    suspend fun incrementEmergencyContactNotifiedCount() {
        dataStore.edit { preferences ->
            val current = preferences[EMERGENCY_CONTACT_NOTIFIED_COUNT] ?: 0
            preferences[EMERGENCY_CONTACT_NOTIFIED_COUNT] = current + 1
        }
    }

    val lastEmergencySimulation: Flow<Long> = dataStore.data.map { preferences ->
        preferences[LAST_EMERGENCY_SIMULATION] ?: 0L
    }

    suspend fun setLastEmergencySimulation(timestamp: Long) {
        dataStore.edit { preferences ->
            preferences[LAST_EMERGENCY_SIMULATION] = timestamp
        }
    }

    // Utility Functions
    suspend fun clearAllPreferences() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun exportPreferences(): Map<String, Any?> {
        val exportedPreferences = mutableMapOf<String, Any?>()
        dataStore.edit { preferences ->
            preferences.asMap().forEach { (key, value) ->
                exportedPreferences[key.name] = value
            }
        }
        return exportedPreferences
    }

    // Check if predictions are currently paused
    fun isPredictionsPaused(): Flow<Boolean> = pausePredictionsUntil.map { timestamp ->
        timestamp > System.currentTimeMillis()
    }

    // Check if any emergency workflow has been triggered recently (within last hour)
    fun isEmergencyCooldownActive(): Flow<Boolean> = lastEmergencySimulation.map { timestamp ->
        timestamp > System.currentTimeMillis() - (60 * 60 * 1000) // 1 hour cooldown
    }
}