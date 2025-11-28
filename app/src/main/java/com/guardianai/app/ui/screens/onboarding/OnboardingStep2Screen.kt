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
fun OnboardingStep2Screen(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress_animation")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.5f,
        animationSpec = tween(800, easing = EaseInOutCubic),
        label = "progress"
    )

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
                text = stringResource(R.string.step_of_4, 2),
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
                text = stringResource(R.string.lifestyle_habits),
                style = GuardianAITypography.OnboardingTitle,
                color = NeonAqua,
                textAlign = TextAlign.Center
            )
        }

        // Form Card
        GlassCard(
            glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.DEFAULT,
            padding = 24.dp
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Lifestyle Questions
                var smoking by remember { mutableStateOf(false) }
                var alcohol by remember { mutableStateOf(false) }
                var proteinIntake by remember { mutableStateOf(0) } // Index for dropdown
                var vegetableFreq by remember { mutableStateOf(0) } // Index for radio
                var waterIntake by remember { mutableStateOf("2.0") }
                var exerciseFreq by remember { mutableStateOf(1) } // Index for dropdown
                var sleepHours by remember { mutableStateOf("7.5") }

                // Smoking Question
                LifestyleQuestion(
                    title = stringResource(R.string.smoking),
                    value = smoking,
                    onValueChange = { smoking = it }
                )

                // Alcohol Question
                LifestyleQuestion(
                    title = stringResource(R.string.alcohol),
                    value = alcohol,
                    onValueChange = { alcohol = it }
                )

                // Protein Intake
                LifestyleDropdown(
                    title = stringResource(R.string.protein_intake),
                    options = listOf(
                        stringResource(R.string.yes),
                        stringResource(R.string.no),
                        stringResource(R.string.occasionally)
                    ),
                    selectedIndex = proteinIntake,
                    onSelected = { proteinIntake = it }
                )

                // Vegetable Frequency
                LifestyleRadio(
                    title = stringResource(R.string.leafy_vegetables),
                    options = listOf(
                        stringResource(R.string.daily),
                        stringResource(R.string.weekly_4_5_times),
                        stringResource(R.string.weekly_2_3_times),
                        stringResource(R.string.rarely)
                    ),
                    selectedIndex = vegetableFreq,
                    onSelected = { vegetableFreq = it }
                )

                // Water Intake
                LifestyleTextField(
                    title = stringResource(R.string.water_intake_liters),
                    value = waterIntake,
                    onValueChange = { waterIntake = it },
                    suffix = "L",
                    icon = Icons.Default.LocalDrink
                )

                // Exercise Frequency
                LifestyleDropdown(
                    title = stringResource(R.string.weekly_exercise),
                    options = listOf(
                        stringResource(R.string.none_exercise),
                        stringResource(R.string.exercise_1_2_times),
                        stringResource(R.string.exercise_3_4_times),
                        stringResource(R.string.exercise_5_plus_times)
                    ),
                    selectedIndex = exerciseFreq,
                    onSelected = { exerciseFreq = it }
                )

                // Sleep Hours
                LifestyleTextField(
                    title = stringResource(R.string.sleep_hours),
                    value = sleepHours,
                    onValueChange = { sleepHours = it },
                    suffix = stringResource(R.string.hours),
                    icon = Icons.Default.Nightlight
                )
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

            // Next Button
            GlassButton(
                onClick = onNext,
                glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.NEON,
                modifier = Modifier.weight(1f),
                height = 56.dp
            ) {
                Text(
                    text = stringResource(R.string.next),
                    style = GuardianAITypography.BodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun LifestyleQuestion(
    title: String,
    value: Boolean,
    onValueChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = GuardianAITypography.BodyLarge,
            color = com.guardianai.app.ui.theme.White
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Yes option
            FilterChip(
                onClick = { if (!value) onValueChange(true) },
                label = { Text(stringResource(R.string.yes)) },
                selected = value,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonAqua.copy(alpha = 0.2f),
                    selectedLabelColor = NeonAqua,
                    unselectedContainerColor = com.guardianai.app.ui.theme.GlassmorphismColors.InteractiveSurface,
                    unselectedLabelColor = com.guardianai.app.ui.theme.White
                )
            )

            // No option
            FilterChip(
                onClick = { if (value) onValueChange(false) },
                label = { Text(stringResource(R.string.no)) },
                selected = !value,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = com.guardianai.app.ui.theme.StatusGreen.copy(alpha = 0.2f),
                    selectedLabelColor = com.guardianai.app.ui.theme.StatusGreen,
                    unselectedContainerColor = com.guardianai.app.ui.theme.GlassmorphismColors.InteractiveSurface,
                    unselectedLabelColor = com.guardianai.app.ui.theme.White
                )
            )
        }
    }
}

@Composable
private fun LifestyleDropdown(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = title,
            style = GuardianAITypography.BodyLarge,
            color = com.guardianai.app.ui.theme.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = options.getOrNull(selectedIndex) ?: "",
                onValueChange = { /* Read-only */ },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonAqua,
                    unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                    cursorColor = NeonAqua
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onSelected(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LifestyleRadio(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            style = GuardianAITypography.BodyLarge,
            color = com.guardianai.app.ui.theme.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedIndex == index,
                        onClick = { onSelected(index) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = NeonAqua,
                            unselectedColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = GuardianAITypography.BodyMedium,
                        color = com.guardianai.app.ui.theme.White
                    )
                }
            }
        }
    }
}

@Composable
private fun LifestyleTextField(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column {
        Text(
            text = title,
            style = GuardianAITypography.BodyLarge,
            color = com.guardianai.app.ui.theme.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            leadingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = title
                )
            },
            suffix = {
                Text(
                    text = suffix,
                    style = GuardianAITypography.BodyMedium,
                    color = com.guardianai.app.ui.theme.White.copy(alpha = 0.7f)
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NeonAqua,
                unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                cursorColor = NeonAqua
            )
        )
    }
}