package com.example.crimewatch.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val badgeCount: Int = 0
) {
    object CrimeReports : BottomNavItem(
        route = "crime_reports",
        label = "Reports",
        icon = Icons.Default.Assignment
    )

    object MissingPersons : BottomNavItem(
        route = "missing_persons",
        label = "Missing",
        icon = Icons.Default.Person
    )

    object Community : BottomNavItem(
        route = "community",
        label = "Community",
        icon = Icons.Default.Group
    )

    object Profile : BottomNavItem(
        route = "profile",
        label = "Profile",
        icon = Icons.Default.AccountCircle
    )

    // Helper function to get all items
    companion object {
        fun values() = listOf(CrimeReports, MissingPersons, Community, Profile)
    }
}
