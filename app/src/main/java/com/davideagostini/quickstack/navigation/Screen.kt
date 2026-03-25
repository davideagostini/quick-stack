package com.davideagostini.quickstack.navigation

// Central route registry for the Compose NavHost. Keep routes here so new feature screens
// add a single source of truth instead of scattering hard-coded strings.
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object SettingsLanguage : Screen("settings/language")
    data object SettingsTimeActions : Screen("settings/time-actions")
}
