package com.guardianai.app.ui.screens.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guardianai.app.R
import com.guardianai.app.domain.models.Gender
import com.guardianai.app.ui.components.glassmorphic.GlassButton
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.theme.GuardianAITypography
import com.guardianai.app.ui.theme.NeonAqua
import com.guardianai.app.ui.theme.ProgressComplete
import com.guardianai.app.ui.theme.ProgressIncomplete
import com.guardianai.app.ui.viewmodel.SharedViewModel

@Composable
fun OnboardingStep1Screen(
    onNext: () -> Unit,
    onBack: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var ageError by remember { mutableStateOf<String?>(null) }
    var heightError by remember { mutableStateOf<String?>(null) }
    var weightError by remember { mutableStateOf<String?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val infiniteTransition = rememberInfiniteTransition(label = "progress_animation")

    // Progress bar animation
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.25f,
        animationSpec = tween(800, easing = EaseInOutCubic),
        label = "progress"
    )

    fun validateAndProceed() {
        fullNameError = null
        ageError = null
        heightError = null
        weightError = null

        var isValid = true

        if (fullName.isBlank()) {
            fullNameError = stringResource(R.string.error_field_required)
            isValid = false
        }

        if (age.isBlank()) {
            ageError = stringResource(R.string.error_field_required)
            isValid = false
        } else {
            val ageNum = age.toIntOrNull()
            if (ageNum == null || ageNum < 15 || ageNum > 100) {
                ageError = stringResource(R.string.error_age_range)
                isValid = false
            }
        }

        if (height.isBlank()) {
            heightError = stringResource(R.string.error_field_required)
            isValid = false
        } else {
            val heightNum = height.toIntOrNull()
            if (heightNum == null || heightNum < 50 || heightNum > 250) {
                heightError = "Please enter a valid height"
                isValid = false
            }
        }

        if (weight.isBlank()) {
            weightError = stringResource(R.string.error_field_required)
            isValid = false
        } else {
            val weightNum = weight.toIntOrNull()
            if (weightNum == null || weightNum < 20 || weightNum > 300) {
                weightError = "Please enter a valid weight"
                isValid = false
            }
        }

        if (isValid) {
            keyboardController?.hide()
            onNext()
        }
    }

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
                text = stringResource(R.string.step_of_4, 1),
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
                text = stringResource(R.string.basic_info),
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
                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = null
                    },
                    label = { Text(stringResource(R.string.full_name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Full Name"
                        )
                    },
                    isError = fullNameError != null,
                    supportingText = fullNameError?.let { { Text(it) } },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonAqua,
                        unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                        errorBorderColor = com.guardianai.app.ui.theme.EmergencyRed,
                        cursorColor = NeonAqua
                    )
                )

                // Age
                OutlinedTextField(
                    value = age,
                    onValueChange = {
                        age = it
                        ageError = null
                    },
                    label = { Text(stringResource(R.string.age)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Age"
                        )
                    },
                    isError = ageError != null,
                    supportingText = ageError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonAqua,
                        unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                        errorBorderColor = com.guardianai.app.ui.theme.EmergencyRed,
                        cursorColor = NeonAqua
                    )
                )

                // Gender Dropdown
                ExposedDropdownMenuBox(
                    expanded = false, // Simplified for now
                    onExpandedChange = { /* Handle expanded change */ }
                ) {
                    OutlinedTextField(
                        value = when (selectedGender) {
                            Gender.MALE -> stringResource(R.string.male)
                            Gender.FEMALE -> stringResource(R.string.female)
                            Gender.OTHER -> stringResource(R.string.other)
                        },
                        onValueChange = { /* Read-only */ },
                        readOnly = true,
                        label = { Text(stringResource(R.string.gender)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = false) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Gender"
                            )
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonAqua,
                            unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                            cursorColor = NeonAqua
                        )
                    )
                }

                // Height and Weight in same row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Height
                    OutlinedTextField(
                        value = height,
                        onValueChange = {
                            height = it
                            heightError = null
                        },
                        label = { Text(stringResource(R.string.height_cm)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Height,
                                contentDescription = "Height"
                            )
                        },
                        isError = heightError != null,
                        supportingText = heightError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonAqua,
                            unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                            errorBorderColor = com.guardianai.app.ui.theme.EmergencyRed,
                            cursorColor = NeonAqua
                        )
                    )

                    // Weight
                    OutlinedTextField(
                        value = weight,
                        onValueChange = {
                            weight = it
                            weightError = null
                        },
                        label = { Text(stringResource(R.string.weight_kg)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.MonitorWeight,
                                contentDescription = "Weight"
                            )
                        },
                        isError = weightError != null,
                        supportingText = weightError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonAqua,
                            unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                            errorBorderColor = com.guardianai.app.ui.theme.EmergencyRed,
                            cursorColor = NeonAqua
                        )
                    )
                }
            }
        }

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back Button (not shown on first step)
            if (false) { // Always false for step 1
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
            }

            // Next Button
            GlassButton(
                onClick = ::validateAndProceed,
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