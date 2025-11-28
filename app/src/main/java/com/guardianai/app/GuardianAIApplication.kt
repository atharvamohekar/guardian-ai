package com.guardianai.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GuardianAIApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize any global services here
        // For example: logging, crash reporting, etc.
    }
}