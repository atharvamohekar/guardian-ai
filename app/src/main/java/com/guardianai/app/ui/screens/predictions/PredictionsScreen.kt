package com.guardianai.app.ui.screens.predictions

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guardianai.app.R
import com.guardianai.app.ui.components.charts.RiskGauge
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.components.glassmorphic.GlassType
import com.guardianai.app.ui.theme.*

@Composable
fun PredictionsScreen(
    paddingValues: PaddingValues,
    onSOSClick: (String) -> Unit,
    onPredictionClick: (String) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "predictions_animation")

    // Simulated predictions data
    var currentRiskLevel by remember { mutableStateOf("moderate") }
    var riskValue by remember { mutableStateOf(0.4f) }

    // Risk level animation
    riskValue by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = when (currentRiskLevel.lowercase()) {
            "low" -> 0.15f
            "moderate" -> 0.4f
            "elevated" -> 0.65f
            "high" -> 0.85f
            "critical" -> 1.0f
            else -> 0.4f
        },
        animationSpec = tween(1500, easing = EaseInOutCubic),
        label = "risk_value"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header with Risk Gauge
        GlassCard(
            glassType = GlassType.NEON,
            padding = 24.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.predictions),
                    style = GuardianAITypography.NeonMedium,
                    color = NeonAqua,
                    textAlign = TextAlign.Center
                )

                RiskGauge(
                    modifier = Modifier.size(200.dp),
                    riskLevel = currentRiskLevel,
                    value = riskValue
                )

                // Risk Level Selector (for demo)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("low", "moderate", "elevated", "high", "critical").forEach { level ->
                        FilterChip(
                            onClick = { currentRiskLevel = level },
                            label = {
                                Text(
                                    text = level.replaceFirstChar { it.uppercase() },
                                    style = GuardianAITypography.BodySmall
                                )
                            },
                            selected = currentRiskLevel == level,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = riskLevelColor(level).copy(alpha = 0.2f),
                                selectedLabelColor = riskLevelColor(level),
                                unselectedContainerColor = GlassmorphismColors.InteractiveSurface,
                                unselectedLabelColor = WhiteSecondary
                            )
                        )
                    }
                }
            }
        }

        // Predictions List
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Recent Insights",
                style = GuardianAITypography.BodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = White,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            // Prediction Cards
            PredictionCard(
                icon = Icons.Default.LocalDrink,
                title = stringResource(R.string.hydration_reminder),
                description = "Based on your activity levels, increase water intake by 500ml today.",
                severity = "low",
                timestamp = "2 hours ago",
                onClick = { onPredictionClick("hydration_001") }
            )

            PredictionCard(
                icon = Icons.Default.LocalFireDepartment,
                title = stringResource(R.string.elevated_stress),
                description = "Stress levels are 30% higher than your baseline. Consider a 5-minute breathing exercise.",
                severity = "moderate",
                timestamp = "4 hours ago",
                onClick = { onPredictionClick("stress_001") }
            )

            PredictionCard(
                icon = Icons.Default.Nightlight,
                title = stringResource(R.string.low_sleep_quality),
                description = "Sleep quality was poor last night. Aim for 8 hours tonight.",
                severity = "elevated",
                timestamp = "1 day ago",
                onClick = { onPredictionClick("sleep_001") }
            )

            PredictionCard(
                icon = Icons.Default.Favorite,
                title = stringResource(R.string.irregular_heart_rate),
                description = "Heart rate pattern shows irregularity. Monitor and contact doctor if persists.",
                severity = "high",
                timestamp = "2 days ago",
                onClick = { onPredictionClick("heart_001") }
            )
        }
    }
}

@Composable
private fun PredictionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    severity: String,
    timestamp: String,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(200, easing = EaseInOutCubic),
        label = "card_scale"
    )

    GlassCard(
        onClick = onClick,
        glassType = when (severity.lowercase()) {
            "low" -> GlassType.SUCCESS
            "moderate" -> GlassType.WARNING
            "elevated" -> GlassType.NEON
            "high" -> GlassType.ERROR
            "critical" -> GlassType.EMERGENCY
            else -> GlassType.DEFAULT
        },
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        cornerRadius = 12.dp,
        padding = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Surface(
                modifier = Modifier.size(48.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = riskLevelColor(severity).copy(alpha = 0.2f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = riskLevelColor(severity),
                    modifier = Modifier.size(24.dp)
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    style = GuardianAITypography.BodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = White
                )

                Text(
                    text = description,
                    style = GuardianAITypography.BodyMedium,
                    color = WhiteSecondary,
                    maxLines = 2
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = timestamp,
                        style = GuardianAITypography.BodySmall,
                        color = WhiteTertiary
                    )

                    SeverityBadge(severity = severity)
                }
            }
        }
    }
}

@Composable
private fun SeverityBadge(severity: String) {
    Surface(
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        color = riskLevelColor(severity).copy(alpha = 0.2f)
    ) {
        Text(
            text = severity.replaceFirstChar { it.uppercase() },
            style = GuardianAITypography.BodySmall.copy(
                fontWeight = FontWeight.Medium
            ),
            color = riskLevelColor(severity),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

private fun riskLevelColor(severity: String): Color {
    return when (severity.lowercase()) {
        "low" -> RiskLow
        "moderate" -> RiskModerate
        "elevated" -> RiskElevated
        "high" -> RiskHigh
        "critical" -> RiskCritical
        else -> RiskLow
    }
}