package com.construction.management.ui.navigation

sealed class Screen(val route: String) {
    // Auth screens
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Main screens
    object Projects : Screen("projects")
    object ProjectDetail : Screen("project_detail")
    object Estimates : Screen("estimates")
    object EstimateDetail : Screen("estimate_detail")
    object Reports : Screen("reports")
    object Materials : Screen("materials")
    object Profile : Screen("profile")
    
    // Admin screens
    object Users : Screen("users")
    object Organizations : Screen("organizations")
    object Analytics : Screen("analytics")
    
    // Additional screens
    object Defects : Screen("defects")
    object Photos : Screen("photos")
    object TimeSheets : Screen("time_sheets")
    object Notifications : Screen("notifications")
}