package com.example.crimewatch.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.crimewatch.ui.components.BottomNavItem

@Composable
fun BottomNavBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            modifier = Modifier.height(80.dp),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp
        ) {
            BottomNavItem.values().forEach { item ->
                val selected = currentRoute == item.route
                
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (item.badgeCount > 0) {
                                    Badge {
                                        Text(
                                            text = item.badgeCount.toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    },
                    label = {
                        AnimatedVisibility(
                            visible = selected,
                            enter = fadeIn(
                                animationSpec = tween(
                                    durationMillis = 200,
                                    easing = FastOutSlowInEasing
                                )
                            ),
                            exit = fadeOut(
                                animationSpec = tween(
                                    durationMillis = 200,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        ) {
                            Text(
                                text = item.label,
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            }
        }
    }
}
