package com.example.crimewatch.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.crimewatch.ui.screens.*
import com.example.crimewatch.ui.components.*
import com.example.crimewatch.viewmodel.AuthViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val startDestination = if (authState.user != null) "main" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Login Flow
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegistrationScreen(
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackClick = {
                    navController.navigateUp()
                }
            )
        }

        // Main App Flow
        navigation(startDestination = BottomNavItem.CrimeReports.route, route = "main") {
            composable(BottomNavItem.CrimeReports.route) {
                CrimeReportsScreen(navController = navController)
            }
            composable(BottomNavItem.Community.route) {
                CommunityScreen(navController)
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    onSignOut = {
                        authViewModel.signOut()
                        navController.navigate("login") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                )
            }
            composable(
                "crime_detail/{reportId}",
                arguments = listOf(navArgument("reportId") { type = NavType.StringType })
            ) { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId") ?: return@composable
                CrimeDetailScreen(
                    reportId = reportId,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable("missing_persons") {
                MissingPersonsScreen(navController)
            }
            composable(
                "missing_person_detail/{personId}",
                arguments = listOf(navArgument("personId") { type = NavType.StringType })
            ) { backStackEntry ->
                val personId = backStackEntry.arguments?.getString("personId")
                MissingPersonDetailScreen(personId)
            }
            composable(
                "community_tip_detail/{tipId}",
                arguments = listOf(navArgument("tipId") { type = NavType.StringType })
            ) { backStackEntry ->
                val tipId = backStackEntry.arguments?.getString("tipId") ?: return@composable
                CommunityTipDetailScreen(
                    tipId = tipId,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
