package com.construction.management.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.construction.management.ui.screens.*
import com.construction.management.ui.screens.auth.LoginScreen
import com.construction.management.ui.screens.auth.RegisterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val currentUser by remember { mutableStateOf(null) } // TODO: Get from ViewModel
    
    if (currentUser == null) {
        AuthNavigation(navController)
    } else {
        MainNavigation(navController)
    }
}

@Composable
fun AuthNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Register.route) {
            RegisterScreen(navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation(navController: NavHostController) {
    val items = listOf(
        NavigationItem(
            title = "Проекты",
            icon = Icons.Filled.Business,
            route = Screen.Projects.route
        ),
        NavigationItem(
            title = "Сметы",
            icon = Icons.Filled.Calculate,
            route = Screen.Estimates.route
        ),
        NavigationItem(
            title = "Отчеты",
            icon = Icons.Filled.Assessment,
            route = Screen.Reports.route
        ),
        NavigationItem(
            title = "Материалы",
            icon = Icons.Filled.Inventory,
            route = Screen.Materials.route
        ),
        NavigationItem(
            title = "Профиль",
            icon = Icons.Filled.Person,
            route = Screen.Profile.route
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Projects.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Projects.route) {
                ProjectsScreen(navController)
            }
            composable(Screen.Estimates.route) {
                EstimatesScreen(navController)
            }
            composable(Screen.Reports.route) {
                ReportsScreen(navController)
            }
            composable(Screen.Materials.route) {
                MaterialsScreen(navController)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController)
            }
            composable(Screen.ProjectDetail.route + "/{projectId}") { backStackEntry ->
                val projectId = backStackEntry.arguments?.getString("projectId")?.toLongOrNull()
                ProjectDetailScreen(navController, projectId)
            }
            composable(Screen.EstimateDetail.route + "/{estimateId}") { backStackEntry ->
                val estimateId = backStackEntry.arguments?.getString("estimateId")?.toLongOrNull()
                EstimateDetailScreen(navController, estimateId)
            }
        }
    }
}

data class NavigationItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)