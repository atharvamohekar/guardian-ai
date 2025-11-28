package com.guardianai.app.ui.screens.auth

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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.guardianai.app.R
import com.guardianai.app.ui.components.glassmorphic.GlassCard
import com.guardianai.app.ui.components.glassmorphic.GlassType
import com.guardianai.app.ui.theme.GuardianAITypography
import com.guardianai.app.ui.theme.NeonAqua
import com.guardianai.app.ui.theme.White
import com.guardianai.app.ui.viewmodel.SharedViewModel

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    val userState by sharedViewModel.userState.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(userState.error) {
        userState.error?.let {
            // Handle error display (could show a snackbar)
        }
    }

    LaunchedEffect(userState.isAuthenticated) {
        if (userState.isAuthenticated) {
            onSignInSuccess()
        }
    }

    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun handleSignIn() {
        emailError = null

        if (email.isBlank()) {
            emailError = stringResource(R.string.error_field_required)
            return
        }

        if (!validateEmail(email)) {
            emailError = stringResource(R.string.error_email_invalid)
            return
        }

        if (password.isBlank()) {
            return
        }

        keyboardController?.hide()
        sharedViewModel.signIn(email, password)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo and Title
        GlassCard(
            glassType = GlassType.NEON,
            padding = 32.dp
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // GuardianAI Logo Icon
                Icon(
                    imageVector = Icons.Default.MonitorHeart,
                    contentDescription = "GuardianAI Logo",
                    tint = NeonAqua,
                    modifier = Modifier.size(64.dp)
                )

                Text(
                    text = stringResource(R.string.app_name),
                    style = GuardianAITypography.NeonLarge,
                    color = NeonAqua,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stringResource(R.string.app_subtitle),
                    style = GuardianAITypography.OnboardingSubtitle,
                    color = White,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Sign In Form
        GlassCard(
            glassType = GlassType.DEFAULT,
            padding = 24.dp
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.sign_in),
                    style = GuardianAITypography.OnboardingTitle,
                    color = White
                )

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text(stringResource(R.string.email_username)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email"
                        )
                    },
                    isError = emailError != null,
                    supportingText = emailError?.let { { Text(it) } },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonAqua,
                        unfocusedBorderColor = White.copy(alpha = 0.3f),
                        errorBorderColor = com.guardianai.app.ui.theme.EmergencyRed,
                        cursorColor = NeonAqua
                    )
                )

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.password)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password"
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility
                                            else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) PasswordVisualTransformation.None
                                        else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonAqua,
                        unfocusedBorderColor = White.copy(alpha = 0.3f),
                        cursorColor = NeonAqua
                    )
                )

                // Sign In Button
                Button(
                    onClick = ::handleSignIn,
                    enabled = !userState.isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (userState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = White
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.sign_in),
                            style = GuardianAITypography.SOSButton
                        )
                    }
                }

                // Sign Up Link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.new_user_create) + " ",
                        style = GuardianAITypography.BodySmall,
                        color = White.copy(alpha = 0.7f)
                    )
                    TextButton(
                        onClick = onNavigateToSignUp,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = NeonAqua
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.sign_up),
                            style = GuardianAITypography.BodySmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Error Display
        userState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            GlassCard(
                glassType = GlassType.ERROR,
                padding = 16.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error",
                        tint = com.guardianai.app.ui.theme.EmergencyRed
                    )
                    Text(
                        text = error,
                        style = GuardianAITypography.BodySmall,
                        color = White
                    )
                }
            }
        }
    }
}