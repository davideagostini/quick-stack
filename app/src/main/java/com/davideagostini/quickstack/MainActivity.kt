package com.davideagostini.quickstack

import android.app.StatusBarManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.davideagostini.quickstack.core.applyAppLanguage
import com.davideagostini.quickstack.core.ui.QuickStackTheme
import com.davideagostini.quickstack.core.ui.surfaceContainerDark
import com.davideagostini.quickstack.core.ui.surfaceContainerLight
import com.davideagostini.quickstack.data.repository.QuickStackSettingsRepository
import com.davideagostini.quickstack.domain.model.QuickItemSource
import com.davideagostini.quickstack.feature.capture.QuickCaptureActivity
import com.davideagostini.quickstack.feature.capture.QuickCaptureActivity.Companion.EXTRA_SUCCESS_MESSAGE
import com.davideagostini.quickstack.feature.capture.QuickCaptureActivity.Companion.extraSource
import com.davideagostini.quickstack.navigation.QuickStackNavigation
import com.davideagostini.quickstack.tile.QuickStackTileService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Main app surface hosting the inbox/history screen.
 *
 * The capture flow itself lives in a separate lightweight Activity so it can be reused by
 * both the launcher entry point and the Quick Settings Tile.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: QuickStackSettingsRepository

    private var pendingSuccessMessage by mutableStateOf<String?>(null)

    private val captureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        pendingSuccessMessage = result.data?.getStringExtra(EXTRA_SUCCESS_MESSAGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyAppLanguage(settingsRepository.currentSettings().languageTag)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                surfaceContainerLight.toArgb(),
                surfaceContainerDark.toArgb(),
            ),
            navigationBarStyle = SystemBarStyle.auto(
                surfaceContainerLight.toArgb(),
                surfaceContainerDark.toArgb(),
            ),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        setContent {
            QuickStackTheme {
                QuickStackNavigation(
                    openCapture = {
                        captureLauncher.launch(
                            Intent(this, QuickCaptureActivity::class.java).apply {
                                putExtra(extraSource, QuickItemSource.APP.name)
                            },
                        )
                    },
                    externalMessage = pendingSuccessMessage,
                    onExternalMessageShown = { pendingSuccessMessage = null },
                )
            }
        }
        requestTileAddIfNeeded()
    }

    private fun requestTileAddIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        if (prefs.getBoolean(KEY_TILE_CONFIRMED, false)) return

        val statusBarManager = getSystemService(StatusBarManager::class.java) ?: return
        statusBarManager.requestAddTileService(
            ComponentName(this, QuickStackTileService::class.java),
            getString(R.string.tile_label),
            Icon.createWithResource(this, R.drawable.ic_tile_quickstack),
            mainExecutor,
        ) { result ->
            val shouldStopPrompting =
                result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ADDED ||
                    result == StatusBarManager.TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED

            if (shouldStopPrompting) {
                prefs.edit { putBoolean(KEY_TILE_CONFIRMED, true) }
            }
        }
    }

    companion object {
        private const val PREFS_NAME = "quickstack_prefs"
        private const val KEY_TILE_CONFIRMED = "tile_confirmed_added_v2"
    }
}
