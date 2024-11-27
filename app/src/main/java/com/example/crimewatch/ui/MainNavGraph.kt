package com.example.crimewatch.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.crimewatch.ui.screens.*
import com.example.crimewatch.ui.components.*

@Composable
fun MainNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    // Assume a flag to check login status
    val isUserLoggedIn = remember { false } // Replace with your actual logic to check login state

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn) "main" else "login",
        modifier = modifier
    ) {
        // Login Flow
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    // Navigate to main flow on login success
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onRegisterClick = {
                    // Navigate to registration
                    navController.navigate("register")
                }
            )
        }
        composable("register") {
            RegistrationScreen(
                onRegisterSuccess = {
                    // Navigate to main flow on registration success
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onBackClick = {
                    // Go back to login
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
                ProfileScreen()
            }
            composable("crime_detail/{reportId}") { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId")
                CrimeDetailScreen(reportId = reportId)
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
            composable("community_tip_detail/{tipId}") { backStackEntry ->
                val tipId = backStackEntry.arguments?.getString("tipId")
                CommunityTipDetailScreen(tipId)
            }
        }
    }
}
