package com.guardianai.app.ui.screens.emergency

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import com.guardianai.app.R
import com.guardianai.app.ui.components.common.EmergencyButton
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.components.glassmorphic.GlassType
import com.guardianai.app.ui.theme.*

@Composable
fun EmergencyCenterScreen(
    incidentId: String,
    onBackToApp: () -> Unit,
    onClose: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emergency_animation")

    // Simulated emergency data
    var emergencyContactNotified by remember { mutableStateOf(false) }
    var hospitalNotified by remember { mutableStateOf(false) }
    var ambulanceDispatched by remember { mutableStateOf(false) }

    // SOS button pulse animation
    val sosScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sos_pulse"
    )

    // Alert color animation
    val alertColor by infiniteTransition.animateColor(
        initialValue = EmergencyRed,
        targetValue = com.guardianai.app.ui.theme.StatusRed,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alert_color"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Emergency Title Card
            GlassCard(
                glassType = GlassType.EMERGENCY,
                padding = 24.dp
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Emergency,
                        contentDescription = stringResource(R.string.emergency_alert_sent),
                        tint = alertColor,
                        modifier = Modifier.size(64.dp)
                    )

                    Text(
                        text = stringResource(R.string.emergency_alert_sent),
                        style = GuardianAITypography.EmergencyTitle,
                        color = alertColor,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "Incident ID: $incidentId",
                        style = GuardianAITypography.BodyMedium,
                        color = com.guardianai.app.ui.theme.WhiteSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Close Button (top right)
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(CircleShape)
                .background(GlassmorphismColors.InteractiveSurface)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = com.guardianai.app.ui.theme.White
            )
        }

        // Emergency Actions (bottom)
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = GlassmorphismColors.Surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Emergency Actions",
                    style = GuardianAITypography.BodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = NeonAqua,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                // Action Status
                EmergencyActionStatus(
                    title = "Emergency Contact",
                    status = if (emergencyContactNotified) "Notified" else "Notifying...",
                    icon = Icons.Default.ContactPhone,
                    isComplete = emergencyContactNotified,
                    onComplete = { emergencyContactNotified = true }
                )

                EmergencyActionStatus(
                    title = "Nearest Hospital",
                    status = "General Hospital (2.3 km away)",
                    icon = Icons.Default.LocalHospital,
                    isComplete = hospitalNotified,
                    onComplete = { hospitalNotified = true }
                )

                EmergencyActionStatus(
                    title = "Ambulance",
                    status = if (ambulanceDispatched) "Dispatched" else "Ready",
                    icon = Icons.Default.Emergency,
                    isComplete = ambulanceDispatched,
                    onComplete = { ambulanceDispatched = true }
                )

                // Call Emergency Services Button
                EmergencyButton(
                    onClick = { /* Call emergency services */ },
                    text = stringResource(R.string.call_emergency_services),
                    enabled = true,
                    isLoading = false
                )

                // Back to App Button
                Button(
                    onClick = onBackToApp,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = com.guardianai.app.ui.theme.StatusGreen,
                        contentColor = com.guardianai.app.ui.theme.White
                    )
                ) {
                    Text(
                        text = "Back to App",
                        style = GuardianAITypography.BodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyActionStatus(
    title: String,
    status: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isComplete: Boolean,
    onComplete: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isComplete) 1.05f else 1f,
        animationSpec = tween(200, easing = EaseInOutCubic),
        label = "status_scale"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
            .background(if (isComplete) GlassmorphismColors.SuccessGlow else GlassmorphismColors.Surface)
            .clickable { if (!isComplete) onComplete() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = if (isComplete) com.guardianai.app.ui.theme.StatusGreen else NeonAqua,
            modifier = Modifier.size(24.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = GuardianAITypography.BodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isComplete) com.guardianai.app.ui.theme.StatusGreen else NeonAqua
            )

            Text(
                text = status,
                style = GuardianAITypography.BodySmall,
                color = com.guardianai.app.ui.theme.WhiteSecondary
            )
        }

        if (isComplete) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Complete",
                tint = com.guardianai.app.ui.theme.StatusGreen,
                modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale }
            )
        } else {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = NeonAqua,
                strokeWidth = 2.dp
            )
        }
    }
}