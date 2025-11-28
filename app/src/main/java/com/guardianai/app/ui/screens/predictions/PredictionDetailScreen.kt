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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guardianai.app.R
import com.guardianai.app.ui.components.charts.SimpleMetricChart
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.components.glassmorphic.GlassType
import com.guardianai.app.ui.theme.*

@Composable
fun PredictionDetailScreen(
    incidentId: String,
    paddingValues: PaddingValues,
    onSOSClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "prediction_detail_animation")

    // Simulated incident data
    val incidentData = remember {
        mapOf(
            "hydration_001" to PredictionData(
                title = "Hydration Reminder",
                severity = "Low",
                timestamp = "2 hours ago",
                detectedValue = "Low water intake detected",
                explanation = "Your activity levels and vital signs indicate you may be dehydrated.",
                recommendations = listOf(
                    "Drink at least 500ml of water now",
                    "Continue monitoring hydration levels",
                    "Set hydration reminders every 2 hours"
                ),
                chartData = listOf(1.2f, 1.5f, 0.8f, 1.0f, 0.9f, 1.3f)
            ),
            "stress_001" to PredictionData(
                title = "Elevated Stress",
                severity = "Moderate",
                timestamp = "4 hours ago",
                detectedValue = "Stress score 30% above baseline",
                explanation = "Your heart rate variability and other metrics indicate elevated stress levels.",
                recommendations = listOf(
                    "Practice 5-minute breathing exercise",
                    "Take a short break from current activity",
                    "Consider stress management techniques",
                    "Monitor stress levels for next 2 hours"
                ),
                chartData = listOf(25f, 35f, 42f, 38f, 45f, 41f, 48f)
            ),
            "heart_001" to PredictionData(
                title = "Irregular Heart Rate",
                severity = "High",
                timestamp = "2 days ago",
                detectedValue = "Heart rate pattern anomaly detected",
                explanation = "Your heart rate shows irregular patterns over the last monitoring period.",
                recommendations = listOf(
                    "Contact your primary doctor immediately",
                    "Avoid strenuous activities",
                    "Monitor heart rate closely",
                    "Keep emergency contact informed"
                ),
                chartData = listOf(72f, 85f, 110f, 95f, 88f, 92f, 105f, 98f)
            )
        )
    }

    val currentIncident = incidentData[incidentId] ?: incidentData.values.first()

    // Fade in animation
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = tween(800, easing = EaseInOutCubic),
        label = "alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.clip(androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonAqua
                )
            }

            Text(
                text = currentIncident.title,
                style = GuardianAITypography.OnboardingTitle,
                color = NeonAqua,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = currentIncident.timestamp,
                style = GuardianAITypography.BodySmall,
                color = com.guardianai.app.ui.theme.WhiteSecondary
            )
        }

        // Detection Info Card
        GlassCard(
            glassType = when (currentIncident.severity.lowercase()) {
                "low" -> GlassType.SUCCESS
                "moderate" -> GlassType.WARNING
                "elevated" -> GlassType.NEON
                "high" -> GlassType.ERROR
                "critical" -> GlassType.EMERGENCY
                else -> GlassType.DEFAULT
            },
            padding = 24.dp
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // What AI Detected
                DetectionSection(
                    icon = Icons.Default.Search,
                    title = stringResource(R.string.what_ai_detected),
                    content = currentIncident.detectedValue
                )

                // Why Suspicious
                DetectionSection(
                    icon = Icons.Default.Psychology,
                    title = stringResource(R.string.why_suspicious),
                    content = currentIncident.explanation
                )

                // Severity Badge
                SeverityBadge(
                    severity = currentIncident.severity,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }

        // Chart Card
        GlassCard(
            glassType = GlassType.DEFAULT,
            padding = 20.dp
        ) {
            Column {
                Text(
                    text = "Recent Trend Analysis",
                    style = GuardianAITypography.BodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = NeonAqua,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                SimpleMetricChart(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    metricType = "trend_data",
                    data = currentIncident.chartData
                )
            }
        }

        // Recommendations Card
        GlassCard(
            glassType = GlassType.SUCCESS,
            padding = 24.dp
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Recommendations",
                        tint = com.guardianai.app.ui.theme.StatusGreen
                    )

                    Text(
                        text = stringResource(R.string.recommendations),
                        style = GuardianAITypography.BodyLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = com.guardianai.app.ui.theme.StatusGreen
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                currentIncident.recommendations.forEachIndexed { index, recommendation ->
                    RecommendationItem(
                        number = index + 1,
                        text = recommendation
                    )

                    if (index < currentIncident.recommendations.size - 1) {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }

        // Close Button
        GlassButton(
            onClick = onBackClick,
            glassType = GlassType.NEON,
            modifier = Modifier.fillMaxWidth(),
            height = 56.dp
        ) {
            Text(
                text = stringResource(R.string.done),
                style = GuardianAITypography.BodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@Composable
private fun DetectionSection(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    content: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = NeonAqua,
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = GuardianAITypography.BodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = NeonAqua
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = content,
                style = GuardianAITypography.BodyMedium,
                color = com.guardianai.app.ui.theme.White
            )
        }
    }
}

@Composable
private fun SeverityBadge(
    severity: String,
    modifier: Modifier = Modifier
) {
    val (color, backgroundColor) = when (severity.lowercase()) {
        "low" -> com.guardianai.app.ui.theme.RiskLow to com.guardianai.app.ui.theme.RiskLow.copy(alpha = 0.2f)
        "moderate" -> com.guardianai.app.ui.theme.RiskModerate to com.guardianai.app.ui.theme.RiskModerate.copy(alpha = 0.2f)
        "elevated" -> com.guardianai.app.ui.theme.RiskElevated to com.guardianai.app.ui.theme.RiskElevated.copy(alpha = 0.2f)
        "high" -> com.guardianai.app.ui.theme.RiskHigh to com.guardianai.app.ui.theme.RiskHigh.copy(alpha = 0.2f)
        "critical" -> com.guardianai.app.ui.theme.RiskCritical to com.guardianai.app.ui.theme.RiskCritical.copy(alpha = 0.2f)
        else -> com.guardianai.app.ui.theme.RiskLow to com.guardianai.app.ui.theme.RiskLow.copy(alpha = 0.2f)
    }

    Surface(
        modifier = modifier,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp),
        color = backgroundColor
    ) {
        Text(
            text = severity.replaceFirstChar { it.uppercase() },
            style = GuardianAITypography.BodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = color,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun RecommendationItem(
    number: Int,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = androidx.compose.foundation.shape.CircleShape,
            color = com.guardianai.app.ui.theme.StatusGreen,
            modifier = Modifier.size(28.dp)
        ) {
            Text(
                text = number.toString(),
                style = GuardianAITypography.BodySmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = com.guardianai.app.ui.theme.White,
                modifier = Modifier.wrapContentSize(Alignment.Center)
            )
        }

        Text(
            text = text,
            style = GuardianAITypography.BodyMedium,
            color = com.guardianai.app.ui.theme.White,
            modifier = Modifier.weight(1f)
        )
    }
}

data class PredictionData(
    val title: String,
    val severity: String,
    val timestamp: String,
    val detectedValue: String,
    val explanation: String,
    val recommendations: List<String>,
    val chartData: List<Float>
)