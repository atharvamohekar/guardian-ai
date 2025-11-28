package com.guardianai.app.ui.components.alerts

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guardianai.app.R
import com.guardianai.app.ui.components.glassmorphic.GlassButton
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.components.glassmorphic.GlassType
import com.guardianai.app.ui.theme.*

@Composable
fun AlertModal(
    isVisible: Boolean,
    title: String,
    message: String,
    severity: String = "moderate",
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onPausePredictions: (Boolean) -> Unit = {},
    onPausePredictionsChange: (Boolean) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "alert_animation")

    // Countdown animation (30 seconds)
    val countdown by infiniteTransition.animateFloat(
        initialValue = 30f,
        targetValue = 0f,
        animationSpec = tween(30000, easing = LinearEasing),
        label = "countdown"
    )

    // Alert color animation
    val alertColor by infiniteTransition.animateColor(
        initialValue = com.guardianai.app.ui.theme.StatusOrange,
        targetValue = com.guardianai.app.ui.theme.EmergencyRed,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_color"
    )

    // Modal animations
    val scale by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "modal_scale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        animationSpec = tween(300, easing = EaseInOutCubic),
        label = "modal_alpha"
    )

    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.8f * alpha))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                glassType = when (severity.lowercase()) {
                    "low" -> GlassType.SUCCESS
                    "moderate" -> GlassType.WARNING
                    "elevated" -> GlassType.NEON
                    "high" -> GlassType.ERROR
                    "critical" -> GlassType.EMERGENCY
                    else -> GlassType.WARNING
                },
                padding = 32.dp,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .graphicsLayer { scaleX = scale; scaleY = scale; alpha = alpha }
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Alert Icon
                    Icon(
                        imageVector = when (severity.lowercase()) {
                            "low" -> Icons.Default.CheckCircle
                            "moderate" -> Icons.Default.Warning
                            "elevated" -> Icons.Default.PriorityHigh
                            "high" -> Icons.Default.Error
                            "critical" -> Icons.Default.Dangerous
                            else -> Icons.Default.Warning
                        },
                        contentDescription = "Alert",
                        tint = alertColor,
                        modifier = Modifier
                            .size(64.dp)
                            .graphicsLayer { scaleX = 1.2f; scaleY = 1.2f }
                    )

                    // Alert Title
                    Text(
                        text = title,
                        style = GuardianAITypography.AlertTitle.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = alertColor,
                        textAlign = TextAlign.Center
                    )

                    // Alert Message
                    Text(
                        text = message,
                        style = GuardianAITypography.AlertMessage,
                        color = com.guardianai.app.ui.theme.White,
                        textAlign = TextAlign.Center
                    )

                    // Countdown Display
                    CountdownDisplay(countdown = countdown.toInt())

                    // Pause Predictions Checkbox
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Checkbox(
                            checked = onPausePredictions,
                            onCheckedChange = onPausePredictionsChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = NeonAqua,
                                uncheckedColor = White.copy(alpha = 0.5f),
                                checkmarkColor = NeonAqua
                            ),
                            modifier = Modifier.padding(end = 12.dp)
                        )

                        Text(
                            text = stringResource(R.string.pause_predictions_1_hour),
                            style = GuardianAITypography.AlertMessage,
                            color = White
                        )
                    }

                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Cancel Button
                        GlassButton(
                            onClick = onCancel,
                            glassType = GlassType.DEFAULT,
                            modifier = Modifier.weight(1f),
                            height = 48.dp
                        ) {
                            Text(
                                text = stringResource(R.string.cancel_alert),
                                style = GuardianAITypography.BodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }

                        // Confirm Button
                        GlassButton(
                            onClick = onConfirm,
                            glassType = GlassType.EMERGENCY,
                            modifier = Modifier.weight(1f),
                            height = 48.dp
                        ) {
                            Text(
                                text = stringResource(R.string.im_not_okay),
                                style = GuardianAITypography.BodyMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountdownDisplay(countdown: Int) {
    GlassCard(
        glassType = GlassType.NEON,
        padding = 16.dp,
        modifier = Modifier.clip(RoundedCornerShape(50.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = if (countdown > 0) {
                    val minutes = countdown / 60
                    val seconds = countdown % 60
                    String.format("%02d:%02d", minutes, seconds)
                } else {
                    "AUTO"
                },
                style = GuardianAITypography.MetricValue.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = NeonAqua,
                textAlign = TextAlign.Center
            )

            Text(
                text = "seconds",
                style = GuardianAITypography.MetricLabel,
                color = WhiteSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Simplified AlertModal for easier usage
@Composable
fun HealthAlertModal(
    isVisible: Boolean,
    incidentType: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    val (title, message, severity) = when (incidentType.lowercase()) {
        "hydration_alert" -> Triple(
            "Hydration Alert",
            "Low water intake detected. Your vitals show signs of dehydration.",
            "moderate"
        )
        "elevated_stress" -> Triple(
            "Elevated Stress Detected",
            "Stress levels 40% above baseline detected. Consider taking a break and practicing breathing exercises.",
            "elevated"
        )
        "irregular_heart_rate" -> Triple(
            "Irregular Heart Rate",
            "Unusual heart rate pattern detected. Monitor closely and contact doctor if persists.",
            "high"
        )
        "low_sleep_quality" -> Triple(
            "Poor Sleep Quality",
            "Sleep quality below optimal range detected. Prioritize rest and maintain consistent sleep schedule.",
            "elevated"
        )
        "emergency" -> Triple(
            "Health Emergency Detected",
            "Critical vital signs detected. Immediate medical attention recommended.",
            "critical"
        )
        else -> Triple(
            "Health Alert",
            "Unusual vitals detected. Please check your health status.",
            "moderate"
        )
    }

    AlertModal(
        isVisible = isVisible,
        title = title,
        message = message,
        severity = severity,
        onConfirm = onConfirm,
        onCancel = onCancel,
        onDismiss = onDismiss
    )
}