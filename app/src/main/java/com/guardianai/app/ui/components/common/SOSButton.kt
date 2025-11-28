package com.guardianai.app.ui.components.common

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.guardianai.app.ui.theme.EmergencyRed
import com.guardianai.app.ui.theme.EmergencyRedDark
import com.guardianai.app.ui.theme.GuardianAITypography
import com.guardianai.app.ui.theme.White
import kotlinx.coroutines.delay

@Composable
fun SOSButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isHighRisk: Boolean = false,
    size: Float = 56f,
    showText: Boolean = true,
    enabled: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sos_animation")

    // Pulse animation for high risk scenarios
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isHighRisk) 1.15f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isHighRisk) 800 else 1200,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = if (isHighRisk) 1f else 0.6f,
        animationSpec = infiniteRepeatable(
            animationSpec = tween(
                durationMillis = if (isHighRisk) 800 else 1200,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(size.dp)
                .shadow(
                    elevation = if (isHighRisk) 12.dp else 8.dp,
                    shape = CircleShape,
                    ambientColor = EmergencyRed.copy(alpha = 0.5f),
                    spotColor = EmergencyRed.copy(alpha = 0.7f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            EmergencyRed.copy(alpha = alpha),
                            EmergencyRedDark.copy(alpha = alpha * 0.8f)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            White.copy(alpha = 0.3f),
                            White.copy(alpha = 0.1f),
                            White.copy(alpha = 0.3f)
                        )
                    ),
                    shape = CircleShape
                )
                .clickable(
                    enabled = enabled,
                    onClick = onClick,
                    interactionSource = interactionSource,
                    indication = null
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isHighRisk) Icons.Default.Warning else Icons.Default.Phone,
                contentDescription = "SOS Emergency",
                tint = White,
                modifier = Modifier.size((size * 0.5f).dp)
            )
        }

        if (showText) {
            Text(
                text = "SOS",
                style = GuardianAITypography.SOSButton.copy(
                    fontSize = (size * 0.25f).sp
                ),
                color = White.copy(alpha = alpha),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmergencyButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    var buttonScale by remember { mutableFloatStateOf(1f) }

    androidx.compose.material3.Button(
        onClick = {
            if (!isLoading) {
                buttonScale = 0.95f
                onClick()
            }
        },
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = MaterialTheme.shapes.medium,
                ambientColor = EmergencyRed.copy(alpha = 0.5f),
                spotColor = EmergencyRed.copy(alpha = 0.7f)
            ),
        enabled = enabled && !isLoading,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = EmergencyRed,
            contentColor = White,
            disabledContainerColor = EmergencyRed.copy(alpha = 0.5f),
            disabledContentColor = White.copy(alpha = 0.5f)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = GuardianAITypography.SOSButton,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Reset button scale after animation
    LaunchedEffect(buttonScale) {
        if (buttonScale < 1f) {
            delay(100)
            buttonScale = 1f
        }
    }
}

@Composable
fun SmallSOSButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isHighRisk: Boolean = false,
    size: Float = 40f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "small_sos_animation")

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isHighRisk) 600 else 1000,
                easing = EaseInOutCubic
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(size.dp)
            .shadow(
                elevation = if (isHighRisk) 6.dp else 4.dp,
                shape = CircleShape,
                ambientColor = EmergencyRed.copy(alpha = 0.4f),
                spotColor = EmergencyRed.copy(alpha = 0.6f)
            )
            .clip(CircleShape)
            .background(
                color = EmergencyRed.copy(alpha = pulseAlpha)
            )
            .border(
                width = 1.5.dp,
                color = White.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Emergency Alert",
            tint = White,
            modifier = Modifier.size((size * 0.6f).dp)
        )
    }
}