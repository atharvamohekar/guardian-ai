package com.guardianai.app.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guardianai.app.R
import com.guardianai.app.ui.components.glassmorphic.GlassButton
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.theme.GuardianAITypography
import com.guardianai.app.ui.theme.NeonAqua
import com.guardianai.app.ui.theme.ProgressComplete
import com.guardianai.app.ui.theme.ProgressIncomplete

@Composable
fun OnboardingStep4Screen(
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress_animation")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1f,
        animationSpec = tween(800, easing = EaseInOutCubic),
        label = "progress"
    )

    var selectedAutonomy by remember { mutableStateOf("fully_automatic") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Progress Indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.step_of_4, 4),
                style = GuardianAITypography.BodySmall,
                color = NeonAqua,
                textAlign = TextAlign.Center
            )

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = ProgressComplete,
                trackColor = ProgressIncomplete,
                strokeCap = StrokeCap.Round
            )

            Text(
                text = stringResource(R.string.autonomy_settings),
                style = GuardianAITypography.OnboardingTitle,
                color = NeonAqua,
                textAlign = TextAlign.Center
            )
        }

        // Autonomy Settings Card
        GlassCard(
            glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.DEFAULT,
            padding = 24.dp
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = "Choose your emergency autonomy level:",
                    style = GuardianAITypography.BodyLarge,
                    color = com.guardianai.app.ui.theme.White,
                    textAlign = TextAlign.Center
                )

                // Autonomy Options
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Semi-Automatic
                    AutonomyCard(
                        title = stringResource(R.string.semi_automatic),
                        description = "Alert requires confirmation before emergency actions",
                        icon = Icons.Default.Security,
                        isSelected = selectedAutonomy == "semi_automatic",
                        glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.WARNING,
                        onClick = { selectedAutonomy = "semi_automatic" },
                        modifier = Modifier.weight(1f)
                    )

                    // Fully Automatic
                    AutonomyCard(
                        title = stringResource(R.string.fully_automatic),
                        description = "Automatic emergency actions for critical scenarios",
                        icon = Icons.Default.AutoMode,
                        isSelected = selectedAutonomy == "fully_automatic",
                        glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.SUCCESS,
                        onClick = { selectedAutonomy = "fully_automatic" },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Critical Actions Display
                GlassCard(
                    glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.DEFAULT,
                    padding = 20.dp
                ) {
                    Column {
                        Text(
                            text = "Emergency Actions Available:",
                            style = GuardianAITypography.BodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = com.guardianai.app.ui.theme.White,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Critical Actions Grid
                        Column(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            CriticalActionRow(
                                icon = Icons.Default.Notifications,
                                title = stringResource(R.string.notify_doctor),
                                description = "Alert your primary doctor"
                            )

                            CriticalActionRow(
                                icon = Icons.Default.Call,
                                title = stringResource(R.string.emergency_call),
                                description = "Call emergency services"
                            )

                            CriticalActionRow(
                                icon = Icons.Default.LocalHospital,
                                title = stringResource(R.string.notify_hospital),
                                description = "Notify nearest hospital"
                            )

                            CriticalActionRow(
                                icon = Icons.Default.Emergency,
                                title = stringResource(R.string.call_ambulance),
                                description = "Request ambulance dispatch"
                            )
                        }
                    }
                }
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button
            TextButton(
                onClick = onBack,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.7f)
                )
            ) {
                Text(
                    text = stringResource(R.string.back),
                    style = GuardianAITypography.BodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            // Finish Setup Button
            GlassButton(
                onClick = onFinish,
                glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.NEON,
                modifier = Modifier.weight(1f),
                height = 56.dp
            ) {
                Text(
                    text = stringResource(R.string.finish_setup),
                    style = GuardianAITypography.BodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun AutonomyCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    glassType: com.guardianai.app.ui.components.glassmorphic.GlassType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(
            durationMillis = 200,
            easing = EaseInOutCubic
        ),
        label = "scale"
    ).value

    GlassCard(
        onClick = onClick,
        glassType = if (isSelected) glassType else com.guardianai.app.ui.components.glassmorphic.GlassType.DEFAULT,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.scale(scale)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) NeonAqua else com.guardianai.app.ui.theme.White.copy(alpha = 0.7f),
                modifier = Modifier.size(48.dp)
            )

            Text(
                text = title,
                style = GuardianAITypography.BodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = if (isSelected) NeonAqua else com.guardianai.app.ui.theme.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = description,
                style = GuardianAITypography.BodySmall,
                color = com.guardianai.app.ui.theme.WhiteSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun CriticalActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = NeonAqua,
            modifier = Modifier.size(24.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = GuardianAITypography.BodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = NeonAqua
            )

            Text(
                text = description,
                style = GuardianAITypography.BodySmall,
                color = com.guardianai.app.ui.theme.WhiteSecondary
            )
        }
    }
}