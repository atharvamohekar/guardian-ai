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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.guardianai.app.R
import com.guardianai.app.domain.models.MedicalCondition
import com.guardianai.app.ui.components.glassmorphic.GlassButton
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.theme.GuardianAITypography
import com.guardianai.app.ui.theme.NeonAqua
import com.guardianai.app.ui.theme.ProgressComplete
import com.guardianai.app.ui.theme.ProgressIncomplete

@Composable
fun OnboardingStep3Screen(
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress_animation")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.75f,
        animationSpec = tween(800, easing = EaseInOutCubic),
        label = "progress"
    )

    var medicalConditions by remember { mutableStateOf(setOf<MedicalCondition>()) }
    var otherConditions by remember { mutableStateOf("") }
    var currentMedications by remember { mutableStateOf("") }

    var emergencyContactName by remember { mutableStateOf("") }
    var emergencyContactPhone by remember { mutableStateOf("") }
    var primaryDoctorName by remember { mutableStateOf("") }
    var primaryDoctorPhone by remember { mutableStateOf("") }

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
                text = stringResource(R.string.step_of_4, 3),
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
                text = stringResource(R.string.medical_profile),
                style = GuardianAITypography.OnboardingTitle,
                color = NeonAqua,
                textAlign = TextAlign.Center
            )
        }

        // Medical Conditions Card
        GlassCard(
            glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.DEFAULT,
            padding = 24.dp
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.known_conditions),
                    style = GuardianAITypography.BodyLarge,
                    color = com.guardianai.app.ui.theme.White
                )

                // Medical Conditions Checkboxes
                val conditions = listOf(
                    MedicalCondition.DIABETES to stringResource(R.string.diabetes),
                    MedicalCondition.HYPERTENSION to stringResource(R.string.hypertension),
                    MedicalCondition.ASTHMA to stringResource(R.string.asthma),
                    MedicalCondition.HEART_DISEASE to stringResource(R.string.heart_disease),
                    MedicalCondition.ARTHRITIS to stringResource(R.string.arthritis),
                    MedicalCondition.ALLERGIES to stringResource(R.string.allergies),
                    MedicalCondition.THYROID_DISORDER to stringResource(R.string.thyroid_disorder)
                )

                // None option (exclusive with others)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = medicalConditions.isEmpty(),
                        onCheckedChange = { none ->
                            if (none) {
                                medicalConditions = emptySet()
                            } else {
                                medicalConditions = setOf(MedicalCondition.NONE)
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = NeonAqua,
                            uncheckedColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.5f)
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.none),
                        style = GuardianAITypography.BodyMedium,
                        color = com.guardianai.app.ui.theme.White
                    )
                }

                // Other conditions
                conditions.forEach { (condition, label) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = condition in medicalConditions,
                            onCheckedChange = { checked ->
                                medicalConditions = if (checked) {
                                    if (condition != MedicalCondition.NONE) {
                                        medicalConditions + condition
                                    } else {
                                        emptySet()
                                    }
                                } else {
                                    medicalConditions - condition
                                }
                            },
                            enabled = !medicalConditions.contains(MedicalCondition.NONE) || condition == MedicalCondition.NONE,
                            colors = CheckboxDefaults.colors(
                                checkedColor = NeonAqua,
                                uncheckedColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.5f)
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = label,
                            style = GuardianAITypography.BodyMedium,
                            color = com.guardianai.app.ui.theme.White
                        )
                    }
                }

                // Other conditions text field (shown when "None" is not selected)
                if (!medicalConditions.contains(MedicalCondition.NONE)) {
                    OutlinedTextField(
                        value = otherConditions,
                        onValueChange = { otherConditions = it },
                        label = { Text(stringResource(R.string.other_conditions)) },
                        placeholder = { Text("Enter any other conditions") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonAqua,
                            unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                            cursorColor = NeonAqua
                        )
                    )
                }

                // Current Medications
                OutlinedTextField(
                    value = currentMedications,
                    onValueChange = { currentMedications = it },
                    label = { Text(stringResource(R.string.current_medications)) },
                    placeholder = { Text("Enter current medications") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonAqua,
                        unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                        cursorColor = NeonAqua
                    )
                )
            }
        }

        // Emergency Contact Card
        GlassCard(
            glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.WARNING,
            padding = 24.dp
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.emergency_contact),
                    style = GuardianAITypography.BodyLarge,
                    color = com.guardianai.app.ui.theme.StatusYellow
                )

                OutlinedTextField(
                    value = emergencyContactName,
                    onValueChange = { emergencyContactName = it },
                    label = { Text(stringResource(R.string.emergency_contact_name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Emergency Contact Name"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.guardianai.app.ui.theme.StatusYellow,
                        unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                        cursorColor = com.guardianai.app.ui.theme.StatusYellow
                    )
                )

                OutlinedTextField(
                    value = emergencyContactPhone,
                    onValueChange = { emergencyContactPhone = it },
                    label = { Text(stringResource(R.string.emergency_contact_phone)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Emergency Contact Phone"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = com.guardianai.app.ui.theme.StatusYellow,
                        unfocusedBorderColor = com.guardianai.app.ui.theme.White.copy(alpha = 0.3f),
                        cursorColor = com.guardianai.app.ui.theme.StatusYellow
                    )
                )
            }
        }

        // Primary Doctor Card (optional)
        GlassCard(
            glassType = com.guardianai.app.ui.components.glassmorphic.GlassType.DEFAULT,
            padding = 24.dp
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.primary_doctor),
                    style = GuardianAITypography.BodyLarge,
                    color = com.guardianai.app.ui.theme.White
                )

                OutlinedTextField(
                    value = primaryDoctorName,
                    onValueChange = { primaryDoctorName = it },
                    label = { Text(stringResource(R.string.primary_doctor_name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Doctor Name"
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

                OutlinedTextField(
                    value = primaryDoctorPhone,
                    onValueChange = { primaryDoctorPhone = it },
                    label = { Text(stringResource(R.string.primary_doctor_phone)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Doctor Phone"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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