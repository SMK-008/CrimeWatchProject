package com.example.crimewatch.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.crimewatch.R

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object CrimeReports : BottomNavItem("crime_reports", Icons.Filled.Star, "Crime Reports")
    object MissingPersons : BottomNavItem("missing_persons", Icons.Filled.Person, "Missing Persons")
    object Community : BottomNavItem("community", Icons.Filled.Info, "Community")
    object Profile : BottomNavItem("profile", Icons.Filled.AccountCircle, "Profile")
}
