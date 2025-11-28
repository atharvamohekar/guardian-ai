package com.guardianai.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.guardianai.app.data.repository.UserProfileRepository
import com.guardianai.app.data.datastore.PreferencesDataStore
import com.guardianai.app.domain.models.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserState(
    val isAuthenticated: Boolean = false,
    val userProfile: UserProfile? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository,
    private val preferencesDataStore: PreferencesDataStore
) : ViewModel() {

    private val _userState = MutableStateFlow(UserState())
    val userState: StateFlow<UserState> = _userState.asStateFlow()

    // Flow for onboarding completion
    val onboardingComplete: Flow<Boolean> = preferencesDataStore.onboardingComplete

    init {
        // Check authentication status on init
        checkAuthenticationStatus()
    }

    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            _userState.update { it.copy(isLoading = true) }

            try {
                val profile = userProfileRepository.getUserProfileSync()
                val onboardingDone = preferencesDataStore.onboardingComplete.first()

                _userState.update {
                    it.copy(
                        isAuthenticated = profile != null,
                        userProfile = profile,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _userState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _userState.update { it.copy(isLoading = true, error = null) }

            try {
                // Simple authentication - check if user exists
                val existingProfile = userProfileRepository.getUserProfileSync()

                if (existingProfile != null) {
                    _userState.update {
                        it.copy(
                            isAuthenticated = true,
                            userProfile = existingProfile,
                            isLoading = false
                        )
                    }
                } else {
                    _userState.update {
                        it.copy(
                            isLoading = false,
                            error = "No account found. Please sign up first."
                        )
                    }
                }
            } catch (e: Exception) {
                _userState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _userState.update { it.copy(isLoading = true, error = null) }

            try {
                // Basic validation
                if (password != confirmPassword) {
                    _userState.update {
                        it.copy(
                            isLoading = false,
                            error = "Passwords do not match"
                        )
                    }
                    return@launch
                }

                if (password.length < 8) {
                    _userState.update {
                        it.copy(
                            isLoading = false,
                            error = "Password must be at least 8 characters"
                        )
                    }
                    return@launch
                }

                // Create a temporary user profile (will be completed in onboarding)
                val tempProfile = UserProfile(
                    fullName = "",
                    age = 0,
                    gender = Gender.MALE,
                    heightCm = 0,
                    weightKg = 0,
                    lifestyle = LifestyleProfile(
                        smoking = false,
                        alcohol = false,
                        proteinIntake = ProteinIntake.NO,
                        leafyVegetableFrequency = VegetableFrequency.RARELY,
                        dailyWaterIntake = 0f,
                        weeklyExerciseFrequency = ExerciseFrequency.NONE,
                        averageSleepHours = 0f
                    ),
                    medicalProfile = MedicalProfile(
                        knownConditions = emptyList(),
                        otherConditions = "",
                        currentMedications = ""
                    ),
                    emergencyContact = EmergencyContact("", ""),
                    primaryDoctor = PrimaryDoctor("", ""),
                    autonomyLevel = AutonomyLevel.SEMI_AUTOMATIC
                )

                userProfileRepository.saveUserProfile(tempProfile)

                _userState.update {
                    it.copy(
                        isAuthenticated = true,
                        userProfile = tempProfile,
                        isLoading = false
                    )
                }

                // Mark first launch as completed
                preferencesDataStore.setFirstLaunch(false)

            } catch (e: Exception) {
                _userState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                _userState.update {
                    it.copy(
                        isAuthenticated = false,
                        userProfile = null,
                        error = null
                    )
                }

                // Optionally clear some sensitive data but keep preferences
                // userProfileRepository.clearUserData()
            } catch (e: Exception) {
                _userState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    fun clearError() {
        _userState.update { it.copy(error = null) }
    }

    fun setOnboardingComplete() {
        viewModelScope.launch {
            try {
                preferencesDataStore.setOnboardingComplete(true)

                // Update existing profile to mark onboarding as complete
                val currentProfile = _userState.value.userProfile
                currentProfile?.let { profile ->
                    val updatedProfile = profile.copy()
                    userProfileRepository.updateUserProfile(updatedProfile)

                    _userState.update {
                        it.copy(userProfile = updatedProfile)
                    }
                }
            } catch (e: Exception) {
                _userState.update {
                    it.copy(error = e.message)
                }
            }
        }
    }

    // Method to get current user profile for other ViewModels
    fun getCurrentUserProfile(): UserProfile? {
        return _userState.value.userProfile
    }
}