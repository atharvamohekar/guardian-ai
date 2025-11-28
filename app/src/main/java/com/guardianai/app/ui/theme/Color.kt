package com.guardianai.app.ui.theme

import androidx.compose.ui.graphics.Color

// Primary Neon Colors
val NeonAqua = Color(0xFF00E5FF)
val NeonAquaDark = Color(0xFF00ACC1)
val NeonAquaLight = Color(0x80FFFFFF)

// Accent Colors
val LimeAccent = Color(0xFFC6FF00)
val LimeAccentDark = Color(0xFF9E9D24)

// SOS/Emergency Colors
val EmergencyRed = Color(0xFFFF1744)
val EmergencyRedDark = Color(0xFFD50000)
val EmergencyRedLight = Color(0xFFFF8A80)

// Status Colors
val StatusGreen = Color(0xFF4CAF50)
val StatusYellow = Color(0xFFFFC107)
val StatusOrange = Color(0xFFFF9800)
val StatusRed = Color(0xFFF44336)

// Background Colors
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceVariant = Color(0xFF2A2A2A)

// Glassmorphism Colors
val GlassSurface = Color(0x33000000)
val GlassSurfaceVariant = Color(0x1A1A1A1A)
val GlassBorder = Color(0x33FFFFFF)
val GlassBorderNeon = Color(0x6600E5FF)
val GlassBorderEmergency = Color(0x66FF1744)

// Text Colors
val White = Color(0xFFFFFFFF)
val WhiteSecondary = Color(0xB3FFFFFF)
val WhiteTertiary = Color(0x80FFFFFF)
val WhiteDisabled = Color(0x4DFFFFFF)

// Chart Colors
val ChartHeartRate = Color(0xFFFF1744)
val ChartSpO2 = Color(0xFF00E5FF)
val ChartStress = Color(0xFFFFC107)
val ChartSleep = Color(0xFF9C27B0)
val ChartSteps = Color(0xFF4CAF50)

// Risk Level Colors
val RiskLow = Color(0xFF4CAF50)
val RiskModerate = Color(0xFFFFC107)
val RiskElevated = Color(0xFFFF9800)
val RiskHigh = Color(0xFFFF5722)
val RiskCritical = Color(0xFFD32F2F)

// Progress Colors
val ProgressComplete = Color(0xFF00E5FF)
val ProgressIncomplete = Color(0xFF2A2A2A)

// Input Field Colors
val InputBackground = Color(0xFF1A1A1A)
val InputBorder = Color(0xFF424242)
val InputBorderFocused = Color(0xFF00E5FF)
val InputBorderError = Color(0xFFFF1744)

// Shadow and Overlay Colors
val ShadowColor = Color(0x80000000)
val OverlayDim = Color(0x66000000)
val OverlayEmergency = Color(0xA6FF1744)

// Glassmorphism Effect Extensions
fun Color.withAlpha(alpha: Float): Color = this.copy(alpha = alpha)

// Glassmorphism color combinations
object GlassmorphismColors {
    val Surface = GlassSurface
    val SurfaceVariant = GlassSurfaceVariant
    val Border = GlassBorder
    val BorderNeon = GlassBorderNeon
    val BorderEmergency = GlassBorderEmergency
    val Background = DarkBackground
    val Overlay = OverlayDim

    // Glass with neon glow effect
    val NeonGlow = NeonAqua.withAlpha(0.12f)
    val NeonBorder = NeonAqua.withAlpha(0.8f)

    // Emergency glass effect
    val EmergencyGlow = EmergencyRed.withAlpha(0.12f)
    val EmergencyBorder = EmergencyRed.withAlpha(0.8f)

    // Content surface glass
    val ContentSurface = Color(0x0A000000)
    val ContentBorder = Color(0x1AFFFFFF)

    // Interactive elements
    val InteractiveSurface = Color(0x1AFFFFFF)
    val InteractiveBorder = Color(0x3DFFFFFF)
    val InteractiveHover = Color(0x2AFFFFFF)

    // Success/Error states
    val SuccessGlow = StatusGreen.withAlpha(0.12f)
    val SuccessBorder = StatusGreen.withAlpha(0.6f)
    val ErrorGlow = StatusRed.withAlpha(0.12f)
    val ErrorBorder = StatusRed.withAlpha(0.6f)
    val WarningGlow = StatusYellow.withAlpha(0.12f)
    val WarningBorder = StatusYellow.withAlpha(0.6f)
}

// Risk level color mapping
fun riskLevelColor(riskLevel: String): Color {
    return when (riskLevel.lowercase()) {
        "low" -> RiskLow
        "moderate" -> RiskModerate
        "elevated" -> RiskElevated
        "high" -> RiskHigh
        "critical" -> RiskCritical
        else -> RiskLow
    }
}

// Status color mapping
fun statusColor(isHealthy: Boolean): Color {
    return if (isHealthy) StatusGreen else StatusOrange
}

// Chart color by metric type
fun chartColorByMetric(metricType: String): Color {
    return when (metricType.lowercase()) {
        "heart_rate", "hr" -> ChartHeartRate
        "spo2", "oxygen" -> ChartSpO2
        "stress" -> ChartStress
        "sleep" -> ChartSleep
        "steps" -> ChartSteps
        else -> NeonAqua
    }
}