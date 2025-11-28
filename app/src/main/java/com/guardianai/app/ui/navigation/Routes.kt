package com.guardianai.app.ui.navigation

sealed class Screen(val route: String) {
    // Authentication screens
    object SignUp : Screen("signup")
    object SignIn : Screen("signin")

    // Onboarding screens
    object OnboardingStep1 : Screen("onboarding1")
    object OnboardingStep2 : Screen("onboarding2")
    object OnboardingStep3 : Screen("onboarding3")
    object OnboardingStep4 : Screen("onboarding4")

    // Main app screens
    object Dashboard : Screen("dashboard")
    object Predictions : Screen("predictions")
    object PredictionDetail : Screen("prediction_detail/{incidentId}") {
        fun createRoute(incidentId: String) = "prediction_detail/$incidentId"
    }
    object Profile : Screen("profile")

    // Emergency screens
    object EmergencyCenter : Screen("emergency_center/{incidentId}") {
        fun createRoute(incidentId: String) = "emergency_center/$incidentId"
    }
}

// Bottom navigation routes (exclude emergency center which is modal)
val bottomNavScreens = listOf(
    Screen.Dashboard,
    Screen.Predictions,
    Screen.Profile
)

// Onboarding flow screens in order
val onboardingScreens = listOf(
    Screen.OnboardingStep1,
    Screen.OnboardingStep2,
    Screen.OnboardingStep3,
    Screen.OnboardingStep4
)

// Authentication screens
val authScreens = listOf(
    Screen.SignUp,
    Screen.SignIn
)

// Main app screens (auth + onboarding + main + emergency)
val allScreens = authScreens + onboardingScreens + bottomNavScreens + listOf(
    Screen.PredictionDetail,
    Screen.EmergencyCenter
)

// Navigation arguments
const val ARG_INCIDENT_ID = "incidentId"

// Deep link patterns
const val DEEP_LINK_EMERGENCY = "guardianai://emergency"
const val DEEP_LINK_PREDICTION = "guardianai://prediction"