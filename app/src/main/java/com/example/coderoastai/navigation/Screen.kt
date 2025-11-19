package com.example.coderoastai.navigation

/**
 * Navigation destinations for the app
 */
sealed class Screen(val route: String, val title: String, val icon: String) {
    object Home : Screen("home", "Home", "🏠")
    object Examples : Screen("examples", "Examples", "📚")
    object History : Screen("history", "History", "🕐")
    object Settings : Screen("settings", "Settings", "⚙️")
}

/**
 * List of bottom navigation items
 */
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Examples,
    Screen.History,
    Screen.Settings
)
