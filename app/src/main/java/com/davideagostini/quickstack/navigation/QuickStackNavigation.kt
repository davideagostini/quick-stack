package com.davideagostini.quickstack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.davideagostini.quickstack.feature.home.HomeScreen
import com.davideagostini.quickstack.feature.settings.LanguageSettingsScreen
import com.davideagostini.quickstack.feature.settings.SettingsScreen
import com.davideagostini.quickstack.feature.settings.TimeActionsSettingsScreen

@Composable
fun QuickStackNavigation(
    openCapture: () -> Unit,
    externalMessage: String?,
    onExternalMessageShown: () -> Unit,
) {
    val navController = rememberNavController()
    // The first milestone only needs one visible route, but the NavHost keeps the app ready
    // for additional screens without changing the entry points again.
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                openCapture = openCapture,
                openSettings = { navController.navigate(Screen.Settings.route) },
                externalMessage = externalMessage,
                onExternalMessageShown = onExternalMessageShown,
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                openLanguageSettings = { navController.navigate(Screen.SettingsLanguage.route) },
                openTimeActionsSettings = { navController.navigate(Screen.SettingsTimeActions.route) },
            )
        }
        composable(Screen.SettingsLanguage.route) {
            LanguageSettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.SettingsTimeActions.route) {
            TimeActionsSettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
