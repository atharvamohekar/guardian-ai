# guardian-ai
# GuardianAI Android App Implementation Plan
## Overview
Complete Android application scaffold with GuardianAI health monitoring system. The app provides predictive health alerts, emergency SOS workflows, and comprehensive user onboarding with a dark glassmorphic UI theme. All data processing and alert logic runs client-side with simulated wearable data feed for consistent demo experience.
Current State Analysis
Based on research, this is a greenfield project requiring complete Android project structure creation. The guardian-ai repository contains only basic git configuration and needs full implementation from scratch.
Data Simulation Strategy
Decision: Hybrid approach for demo consistency - use simulated wearable data by default with optional real sensor integration for future enhancement. This ensures reliable demonstrations while keeping architecture flexible.

## Project Architecture & Structure
Repository Structure <br>
guardian-ai/  <br>
├── app/  <br>
│   ├── src/  <br>
│   │   ├── main/  <br>
│   │   │   ├── java/com/guardianai/app/  <br>
│   │   │   │   ├── MainActivity.kt  <br>
│   │   │   │   ├── GuardianAIApplication.kt <br>
│   │   │   │   ├── di/                     // Hilt modules <br>
│   │   │   │   ├── ui/  <br>
│   │   │   │   │   ├── screens/           // All screen composables <br>
│   │   │   │   │   ├── components/        // Reusable UI components <br>
│   │   │   │   │   ├── theme/             // Theme, colors, typography <br>
│   │   │   │   │   └── navigation/        // Navigation setup <br>
│   │   │   │   ├── data/ <br>
│   │   │   │   │   ├── database/          // Room entities, DAOs, database <br>
│   │   │   │   │   ├── datastore/         // DataStore preferences <br>
│   │   │   │   │   └── repository/        // Repository implementations <br>
│   │   │   │   ├── domain/ <br>
│   │   │   │   │   ├── models/            // Domain models <br>
│   │   │   │   │   └── usecases/          // Use cases <br>
│   │   │   │   ├── feature/ <br>
│   │   │   │   │   ├── agent/             // Prediction and alert logic <br>
│   │   │   │   │   └── simulator/         // Wearable data simulation <br>
│   │   │   │   └── util/                  // Utilities, helpers <br>
│   │   │   ├── res/                       // Android resources <br>
│   │   │   └── AndroidManifest.xml <br>
│   │   ├── test/                          // Unit tests <br>
│   │   └── androidTest/                   // Instrumentation tests <br>
│   ├── build.gradle.kts <br>
│   └── proguard-rules.pro <br>
├── build.gradle.kts <br>
├── gradle.properties <br>
├── settings.gradle.kts <br>
└── README.md <br>

## Technology Stack
- Language: Kotlin
- UI: Jetpack Compose
- Architecture: MVVM + Repository Pattern
- Dependency Injection: Hilt
- Database: Room + DataStore
- Navigation: Jetpack Navigation Compose
- Async: Coroutines + Flow
- Background: WorkManager (optional)
- Emergency Workflow Strategy
- Decision: Auto-call emergency services for high-risk scenarios. This provides maximum protection for critical health situations while maintaining user control     through autonomy settings and alert cancellation options.
- UI Design Philosophy
- Glassmorphic Dark Theme with neon accents demonstrated in the mockup:
- Background: Dark gradient with blur effects
- Cards: Translucent panels with soft neon borders (#00E5FF)
- Primary colors: Neon Aqua/Blue (#00E5FF), Lime/Yellow accents, Red (#ff1744) for SOS
- Typography: Roboto/Google Sans with varying weights
- Interactive elements: Rounded corners, subtle animations, haptic feedback
- Health Alert Strategy
- Decision: Hybrid approach using standard medical thresholds with user customization options. This provides safety through established medical guidelines while allowing personalization based on individual health conditions and doctor recommendations.

## Core Components Implementation
1. Database Schema & Data Layer
Room Database Entities
UserProfile Entity
  Fields: id, fullName, age, gender, height, weight,
 lifestyle: smoking, alcohol, proteinIntake, leafyVegFreq,
  waterIntake, exerciseFreq, sleepHours,
 medical: knownConditions, medications, emergencyContact,
 primaryDoctor, autonomyLevel, createdAt, updatedAt

VitalsSample Entity
 Fields: id, userId, timestamp, heartRate, spO2, stressScore,
 steps, sleepHours, dataSource (simulated/real)

PredictionIncident Entity
 Fields: id, userId, timestamp, incidentType, severity,
 detectedMetrics, explanation, recommendations, resolved

EmergencyLog Entity
 Fields: id, userId, timestamp, incidentId, actions,
 contactsNotified, emergencyServicesCalled, location

DataStore Preferences
onboardingComplete: Boolean
autonomyMode: String ("semi-automatic" | "fully-automatic")
pausePredictionsUntil: Long (timestamp)
developerMode: Boolean
timeCompressionFactor: Int (15min -> 30s default)

3. Navigation Architecture
Route Structure
sealed class Screen(val route: String) {
    object SignUp : Screen("signup")
    object SignIn : Screen("signin")
    object OnboardingStep1 : Screen("onboarding1")
    object OnboardingStep2 : Screen("onboarding2")
    object OnboardingStep3 : Screen("onboarding3")
    object OnboardingStep4 : Screen("onboarding4")
    object Dashboard : Screen("dashboard")
    object Predictions : Screen("predictions")
    object PredictionDetail : Screen("prediction_detail/{incidentId}")
    object Profile : Screen("profile")
    object EmergencyCenter : Screen("emergency_center/{incidentId}")
}

## Navigation Flow
Single NavHost with conditional bottom navigation
Modal navigation for EmergencyCenter (no bottom nav)
AlertModal as composable overlay (not a route)
Deep linking support for emergency scenarios

4. Screen Specifications
Authentication Screens
SignUpScreen (/signup)
Logo: GuardianAI with subtitle "Your Personal Health Sentinel"
Form fields: Email/Username, Password, Confirm Password
Validation: Email format, password length (min 8), password match
Primary button: "Sign Up" (neon blue)
Footer link: "Already an existing user? Sign In" → navigates to SignInScreen
Success: Navigate to OnboardingStep1_BasicInfo
SignInScreen (/signin)
Same branding as SignUpScreen
Form fields: Email/Username, Password
Primary button: "Sign In"
Footer link: "New user? Create Account" → navigates to SignUpScreen
Success flow: Check onboarding completion → Dashboard or OnboardingStep1
Onboarding Flow
OnboardingStep1_BasicInfo
Progress indicator: "Step 1 of 4" (25% fill, neon blue)
Fields: Full Name (required), Age (15-100), Gender (dropdown), Height (cm), Weight (kg)
Navigation: Back (text) + Next (primary button)
Validation: Required fields, numeric ranges, dropdown selection
Save: All fields to UserProfile entity
OnboardingStep2_LifestyleHabits
Progress: "Step 2 of 4" (50% fill)
Toggle fields: Smoking (Yes/No), Alcohol (Yes/No)
Dropdown: Protein Intake (Yes/No/Occasionally with examples)
Radio: Leafy Vegetable Frequency (Daily/4-5x/2-3x/Rarely)
Numeric: Daily Water Intake (liters)
Dropdown: Weekly Exercise (None/1-2/3-4/5+)
Numeric: Average Sleep Hours
Save: All lifestyle fields to UserProfile
OnboardingStep3_MedicalProfile
Progress: "Step 3 of 4" (75% fill)
Checkboxes: Known Conditions (Diabetes, Hypertension, Asthma, etc.)
Logic: "None" checkbox disables others
Text area: "Other conditions" (multiline, optional)
Text input: Current Medications
Contact fields: Emergency Contact (name+phone, required), Primary Doctor (name+phone, optional)
Save: Medical profile to UserProfile
OnboardingStep4_AutonomySettings
Progress: "Step 4 of 4" (100% fill)
Radio cards: Semi-Automatic vs Fully Automatic (default)
Critical actions display: Notify Doctor, Emergency Call, Notify Hospital, Call Ambulance
Finish Setup button → saves autonomyLevel, onboardingComplete = true
Navigation: Navigate to DashboardScreen
Main Application Screens
DashboardScreen (/dashboard)
Header: "Hello, {User Name}" (left), SOS button (right, red, pulsing if high risk)
Metrics Grid (2x2): Heart Rate, SpO₂, Stress Level, Sleep Hours
Chart Area: Line chart with filter tabs (HR/SpO₂/Stress/Sleep/Steps)
Info text: "Metrics refresh every 15 minutes. If anomalies detected..."
Bottom Navigation: Dashboard (active), Predictions, Profile
PredictionsScreen (/predictions)
Header: "Predictions" (left), SOS button (right)
Risk Gauge: Semi-circular gauge color-coded (Green/Yellow/Orange/Red)
Insight Cards List: Hydration Reminder, Elevated Stress, Low Sleep Quality, Irregular Heart Rate
Card interaction: Tap → PredictionDetailScreen with incident data
Bottom Navigation: Dashboard, Predictions (active), Profile
PredictionDetailScreen (/prediction_detail/{incidentId})
Header: Back button (left), SOS button (right), Title + Timestamp
Detection Box: "What AI detected", "Why suspicious", "Severity" (color label)
Recommendations Card: Actionable steps based on incident type
Mini Trend Chart: Last 15-60 minutes with anomaly annotation
Close/Done button: Return to PredictionsScreen
ProfileScreen (/profile)
Header: "Profile" (left), SOS button (right), Edit icon
Information Cards: Basic Info, Lifestyle Habits, Medical Profile, Emergency Contacts
Edit Mode: Fields become inputs, Save/Cancel buttons appear
Data persistence: Save to Room database on changes
Emergency Flow
EmergencyCenterScreen (/emergency_center/{incidentId})
Modal-like overlay: No bottom navigation, full-screen experience
Title: "Emergency Alert Sent"
Vitals Summary Card: Current HR, SpO₂, Stress, last updated time
Map/Location: Static map with nearest hospital pin, distance display
Emergency Contact Card: Shows contact notified status
Call Button: Floating red button to open phone dialer
Message Center: "Emergency services notified. Help is on the way."
AlertModal (Overlay Component)
Backdrop: Semi-transparent with blur effect
Modal: Centered glassmorphic panel, non-dismissible via outside touch
Content: Alert title, explanation text, 30-second countdown timer
Checkbox: "Pause predictions for 1 hour"
Buttons: "I'm Not OK" (opens EmergencyCenter), "Cancel" (dismisses, saves pause preference)
Audio: Full-volume alarm sound using AlarmManager/MediaPlayer

6. Agent Logic & Predictive System
Health Threshold Rules (Hybrid Approach)
Standard Medical Thresholds (Base Values)
Heart Rate: 60-100 bpm (normal), >100 (elevated), >120 (concerning)
SpO₂: >95% (normal), 94-95% (monitor), <94% (alert)
Stress Score: 0-30 (low), 31-60 (moderate), >60 (high)
Sleep Hours: 7-9 hours (optimal), <6 or >10 (monitor)
Personalized Adjustments
Age-based HR adjustment: (220 - age) max HR calculation
Condition-specific thresholds: Adjust for known medical conditions
User customizations: Allow ±10% adjustment to base thresholds
Lifestyle factors: Smoking/alcohol may modify alert sensitivit
