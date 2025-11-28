package com.guardianai.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.guardianai.app.R
import com.guardianai.app.ui.components.common.SmallSOSButton
import com.guardianai.app.ui.screens.auth.SignInScreen
import com.guardianai.app.ui.screens.auth.SignUpScreen
import com.guardianai.app.ui.screens.dashboard.DashboardScreen
import com.guardianai.app.ui.screens.emergency.EmergencyCenterScreen
import com.guardianai.app.ui.screens.onboarding.*
import com.guardianai.app.ui.screens.predictions.PredictionDetailScreen
import com.guardianai.app.ui.screens.predictions.PredictionsScreen
import com.guardianai.app.ui.screens.profile.ProfileScreen
import com.guardianai.app.ui.viewmodel.SharedViewModel

@Composable
fun GuardianAINavigation(
    navController: NavController = rememberNavController(),
    sharedViewModel: SharedViewModel = hiltViewModel()
) {
    // Collect user authentication state
    val userState by sharedViewModel.userState.collectAsStateWithLifecycle()
    val onboardingComplete by sharedViewModel.onboardingComplete.collectAsStateWithLifecycle()

    // Determine start destination
    val startDestination = when {
        !userState.isUserAuthenticated -> Screen.SignUp.route
        userState.isUserAuthenticated && !onboardingComplete -> Screen.OnboardingStep1.route
        else -> Screen.Dashboard.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.fillMaxSize()
    ) {
        // Authentication Screens
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.OnboardingStep1.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onNavigateToSignIn = {
                    navController.navigate(Screen.SignIn.route)
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignInScreen(
                onSignInSuccess = {
                    if (onboardingComplete) {
                        navController.navigate(Screen.Dashboard.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.OnboardingStep1.route) {
                            popUpTo(Screen.SignIn.route) { inclusive = true }
                        }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding Screens
        onboardingNavigation(
            navController = navController,
            sharedViewModel = sharedViewModel
        )

        // Main App Screens with Bottom Navigation
        composable(Screen.Dashboard.route) {
            MainScreenLayout(
                navController = navController,
                currentRoute = Screen.Dashboard.route,
                showBottomNav = true
            ) { paddingValues ->
                DashboardScreen(
                    paddingValues = paddingValues,
                    onSOSClick = { incidentId ->
                        navController.navigate(Screen.EmergencyCenter.createRoute(incidentId))
                    },
                    onPredictionClick = { incidentId ->
                        navController.navigate(Screen.PredictionDetail.createRoute(incidentId))
                    }
                )
            }
        }

        composable(Screen.Predictions.route) {
            MainScreenLayout(
                navController = navController,
                currentRoute = Screen.Predictions.route,
                showBottomNav = true
            ) { paddingValues ->
                PredictionsScreen(
                    paddingValues = paddingValues,
                    onSOSClick = { incidentId ->
                        navController.navigate(Screen.EmergencyCenter.createRoute(incidentId))
                    },
                    onPredictionClick = { incidentId ->
                        navController.navigate(Screen.PredictionDetail.createRoute(incidentId))
                    }
                )
            }
        }

        composable(Screen.Profile.route) {
            MainScreenLayout(
                navController = navController,
                currentRoute = Screen.Profile.route,
                showBottomNav = true
            ) { paddingValues ->
                ProfileScreen(
                    paddingValues = paddingValues,
                    onSOSClick = { incidentId ->
                        navController.navigate(Screen.EmergencyCenter.createRoute(incidentId))
                    }
                )
            }
        }

        // Detail Screen (no bottom nav)
        composable(
            route = Screen.PredictionDetail.route,
            arguments = listOf(navArgument(ARG_INCIDENT_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            MainScreenLayout(
                navController = navController,
                currentRoute = Screen.PredictionDetail.route,
                showBottomNav = false,
                backStackEntry = backStackEntry
            ) { paddingValues ->
                val incidentId = backStackEntry.arguments?.getString(ARG_INCIDENT_ID) ?: ""
                PredictionDetailScreen(
                    incidentId = incidentId,
                    paddingValues = paddingValues,
                    onSOSClick = { incidentId ->
                        navController.navigate(Screen.EmergencyCenter.createRoute(incidentId))
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }

        // Emergency Center Screen (modal, no bottom nav)
        composable(
            route = Screen.EmergencyCenter.route,
            arguments = listOf(navArgument(ARG_INCIDENT_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val incidentId = backStackEntry.arguments?.getString(ARG_INCIDENT_ID) ?: ""
            EmergencyCenterScreen(
                incidentId = incidentId,
                onBackToApp = {
                    navController.popBackStack(Screen.Dashboard.route, false)
                },
                onClose = {
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
private fun MainScreenLayout(
    navController: NavController,
    currentRoute: String,
    showBottomNav: Boolean,
    backStackEntry: NavBackStackEntry? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomNav && currentRoute in bottomNavScreens.map { it.route }) {
                BottomNavigationBar(
                    navController = navController,
                    currentRoute = currentRoute
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            content(paddingValues)

            // SOS button in top-right corner for most screens
            if (currentRoute in listOf(
                Screen.Dashboard.route,
                Screen.Predictions.route,
                Screen.Profile.route,
                Screen.PredictionDetail.route
            )) {
                SmallSOSButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    onClick = { /* Handle SOS click */ },
                    isHighRisk = false // This would come from sharedViewModel
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        NavigationBar {
            bottomNavScreens.forEach { screen ->
                NavigationBarItem(
                    icon = { NavigationIcon(screen.route) },
                    label = { NavigationLabel(screen.route) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        if (currentRoute != screen.route) {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    }
}

@Composable
private fun NavigationIcon(route: String): ImageVector {
    return when (route) {
        Screen.Dashboard.route -> Icons.Default.Dashboard
        Screen.Predictions.route -> Icons.Default.Analytics
        Screen.Profile.route -> Icons.Default.Person
        else -> Icons.Default.Home
    }
}

@Composable
private fun NavigationLabel(route: String): String {
    return when (route) {
        Screen.Dashboard.route -> stringResource(R.string.dashboard)
        Screen.Predictions.route -> stringResource(R.string.predictions)
        Screen.Profile.route -> stringResource(R.string.profile)
        else -> stringResource(R.string.dashboard)
    }
}

private fun NavGraphBuilder.onboardingNavigation(
    navController: NavController,
    sharedViewModel: SharedViewModel
) {
    composable(Screen.OnboardingStep1.route) {
        OnboardingStep1Screen(
            onNext = { navController.navigate(Screen.OnboardingStep2.route) },
            onBack = { /* No back for first step */ }
        )
    }

    composable(Screen.OnboardingStep2.route) {
        OnboardingStep2Screen(
            onNext = { navController.navigate(Screen.OnboardingStep3.route) },
            onBack = { navController.popBackStack() }
        )
    }

    composable(Screen.OnboardingStep3.route) {
        OnboardingStep3Screen(
            onNext = { navController.navigate(Screen.OnboardingStep4.route) },
            onBack = { navController.popBackStack() }
        )
    }

    composable(Screen.OnboardingStep4.route) {
        OnboardingStep4Screen(
            onFinish = {
                // Mark onboarding complete
                sharedViewModel.setOnboardingComplete()
                navController.navigate(Screen.Dashboard.route) {
                    popUpTo(Screen.SignUp.route) { inclusive = true }
                }
            },
            onBack = { navController.popBackStack() }
        )
    }
}