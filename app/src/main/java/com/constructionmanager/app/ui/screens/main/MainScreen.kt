package com.constructionmanager.app.ui.screens.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.constructionmanager.app.ui.screens.dashboard.DashboardScreen
import com.constructionmanager.app.ui.screens.projects.ProjectsScreen
import com.constructionmanager.app.ui.screens.estimates.EstimatesScreen
import com.constructionmanager.app.ui.screens.reports.ReportsScreen
import com.constructionmanager.app.ui.screens.issues.IssuesScreen
import com.constructionmanager.app.ui.screens.photos.PhotosScreen
import com.constructionmanager.app.ui.screens.profile.ProfileScreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Dashboard : BottomNavItem("dashboard", "Главная", Icons.Default.Dashboard)
    object Projects : BottomNavItem("projects", "Проекты", Icons.Default.Business)
    object Estimates : BottomNavItem("estimates", "Сметы", Icons.Default.Calculate)
    object Reports : BottomNavItem("reports", "Отчеты", Icons.Default.Assessment)
    object Issues : BottomNavItem("issues", "Замечания", Icons.Default.Warning)
    object Photos : BottomNavItem("photos", "Фото", Icons.Default.PhotoCamera)
    object Profile : BottomNavItem("profile", "Профиль", Icons.Default.Person)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.Dashboard,
        BottomNavItem.Projects,
        BottomNavItem.Estimates,
        BottomNavItem.Reports,
        BottomNavItem.Issues,
        BottomNavItem.Photos,
        BottomNavItem.Profile
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
            startDestination = BottomNavItem.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Dashboard.route) {
                DashboardScreen()
            }
            composable(BottomNavItem.Projects.route) {
                ProjectsScreen()
            }
            composable(BottomNavItem.Estimates.route) {
                EstimatesScreen()
            }
            composable(BottomNavItem.Reports.route) {
                ReportsScreen()
            }
            composable(BottomNavItem.Issues.route) {
                IssuesScreen()
            }
            composable(BottomNavItem.Photos.route) {
                PhotosScreen()
            }
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(onLogout = onLogout)
            }
        }
    }
}