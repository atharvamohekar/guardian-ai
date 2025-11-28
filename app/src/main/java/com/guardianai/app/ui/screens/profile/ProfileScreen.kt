package com.guardianai.app.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guardianai.app.R
import com.guardianai.app.ui.components.glassmorphic.GlassButton
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.theme.*

@Composable
fun ProfileScreen(
    paddingValues: PaddingValues,
    onSOSClick: (String) -> Unit
) {
    var isEditMode by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("John Doe") }
    var age by remember { mutableStateOf("35") }
    var gender by remember { mutableStateOf("Male") }
    var height by remember { mutableStateOf("175") }
    var weight by remember { mutableStateOf("70") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        ProfileHeader(
            isEditMode = isEditMode,
            onEditToggle = { isEditMode = !isEditMode },
            onSave = { /* Save profile data */ },
            onCancel = { /* Cancel editing */ }
        )

        // Profile Sections
        if (isEditMode) {
            EditableProfileSections(
                fullName = fullName,
                onFullNameChange = { fullName = it },
                age = age,
                onAgeChange = { age = it },
                gender = gender,
                onGenderChange = { gender = it },
                height = height,
                onHeightChange = { height = it },
                weight = weight,
                onWeightChange = { weight = it }
            )
        } else {
            ReadOnlyProfileSections(
                fullName = fullName,
                age = age,
                gender = gender,
                height = height,
                weight = weight
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    isEditMode: Boolean,
    onEditToggle: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    GlassCard(
        glassType = GlassType.NEON,
        padding = 20.dp
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile),
                style = GuardianAITypography.OnboardingTitle,
                color = NeonAqua
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isEditMode) {
                    // Cancel Button
                    TextButton(
                        onClick = onCancel,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = White.copy(alpha = 0.7f)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = GuardianAITypography.BodyMedium
                        )
                    }

                    // Save Button
                    GlassButton(
                        onClick = onSave,
                        glassType = GlassType.SUCCESS,
                        height = 40.dp
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            style = GuardianAITypography.BodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                } else {
                    // Edit Button
                    IconButton(
                        onClick = onEditToggle
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = NeonAqua
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReadOnlyProfileSections(
    fullName: String,
    age: String,
    gender: String,
    height: String,
    weight: String
) {
    // Basic Info Section
    ProfileSectionCard(
        title = stringResource(R.string.basic_info_title),
        icon = Icons.Default.Person
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileInfoRow(label = "Full Name", value = fullName)
            ProfileInfoRow(label = "Age", value = "$age years")
            ProfileInfoRow(label = "Gender", value = gender)
            ProfileInfoRow(label = "Height", value = "${height} cm")
            ProfileInfoRow(label = "Weight", value = "${weight} kg")
        }
    }

    // Lifestyle Section
    ProfileSectionCard(
        title = stringResource(R.string.lifestyle_title),
        icon = Icons.Default.DirectionsRun
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileInfoRow(label = "Smoking", value = "No")
            ProfileInfoRow(label = "Alcohol", value = "Occasionally")
            ProfileInfoRow(label = "Protein Intake", value = "Daily")
            ProfileInfoRow(label = "Exercise", value = "3-4 times/week")
            ProfileInfoRow(label = "Sleep", value = "7.5 hours/night")
        }
    }

    // Medical Section
    ProfileSectionCard(
        title = stringResource(R.string.medical_profile_title),
        icon = Icons.Default.LocalHospital
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileInfoRow(label = "Known Conditions", value = "None")
            ProfileInfoRow(label = "Current Medications", value = "Vitamin D supplement")
        }
    }

    // Emergency Contacts Section
    ProfileSectionCard(
        title = stringResource(R.string.emergency_contacts_title),
        icon = Icons.Default.ContactPhone,
        glassType = GlassType.WARNING
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileInfoRow(label = "Emergency Contact", value = "Jane Doe - +1234567890")
            ProfileInfoRow(label = "Primary Doctor", value = "Dr. Smith - +0987654321")
        }
    }
}

@Composable
private fun EditableProfileSections(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    height: String,
    onHeightChange: (String) -> Unit,
    weight: String,
    onWeightChange: (String) -> Unit
) {
    // Basic Info Section (Editable)
    ProfileSectionCard(
        title = stringResource(R.string.basic_info_title),
        icon = Icons.Default.Person
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Full Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonAqua,
                    unfocusedBorderColor = White.copy(alpha = 0.3f),
                    cursorColor = NeonAqua
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = age,
                    onValueChange = onAgeChange,
                    label = { Text("Age") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonAqua,
                        unfocusedBorderColor = White.copy(alpha = 0.3f),
                        cursorColor = NeonAqua
                    )
                )

                OutlinedTextField(
                    value = height,
                    onValueChange = onHeightChange,
                    label = { Text("Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonAqua,
                        unfocusedBorderColor = White.copy(alpha = 0.3f),
                        cursorColor = NeonAqua
                    )
                )

                OutlinedTextField(
                    value = weight,
                    onValueChange = onWeightChange,
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonAqua,
                        unfocusedBorderColor = White.copy(alpha = 0.3f),
                        cursorColor = NeonAqua
                    )
                )
            }

            // Gender Dropdown (simplified)
            OutlinedTextField(
                value = gender,
                onValueChange = onGenderChange,
                label = { Text("Gender") },
                readOnly = true,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonAqua,
                    unfocusedBorderColor = White.copy(alpha = 0.3f),
                    cursorColor = NeonAqua
                )
            )
        }
    }

    // Lifestyle Section (Editable)
    ProfileSectionCard(
        title = stringResource(R.string.lifestyle_title),
        icon = Icons.Default.DirectionsRun
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add editable lifestyle fields here
            Text(
                text = "Lifestyle preferences editing coming soon...",
                style = GuardianAITypography.BodyMedium,
                color = WhiteSecondary
            )
        }
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    glassType: GlassType = GlassType.DEFAULT,
    content: @Composable () -> Unit
) {
    GlassCard(
        glassType = glassType,
        padding = 20.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = NeonAqua,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = title,
                    style = GuardianAITypography.BodyLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = NeonAqua
                )
            }

            content()
        }
    }
}

@Composable
private fun ProfileInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = GuardianAITypography.BodyMedium,
            color = WhiteSecondary
        )

        Text(
            text = value,
            style = GuardianAITypography.BodyMedium,
            color = White
        )
    }
}