package com.davideagostini.quickstack.feature.capture

import android.app.Activity
import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.davideagostini.quickstack.core.applyAppLanguage
import com.davideagostini.quickstack.core.hasNotificationPermission
import com.davideagostini.quickstack.core.ui.QuickStackTheme
import com.davideagostini.quickstack.core.ui.surfaceContainerDark
import com.davideagostini.quickstack.core.ui.surfaceContainerLight
import com.davideagostini.quickstack.data.repository.QuickStackSettingsRepository
import com.davideagostini.quickstack.domain.model.QuickItemSource
import com.davideagostini.quickstack.feature.capture.model.CaptureEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Full-screen Activity used for quick capture.
 *
 * It is opened both from the launcher flow and from the Quick Settings Tile, and keeps
 * the flow intentionally short so capture can finish in a few taps.
 */
@AndroidEntryPoint
class QuickCaptureActivity : ComponentActivity() {
    @Inject
    lateinit var settingsRepository: QuickStackSettingsRepository

    private var pendingPermissionEvent: CaptureEvent? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        val event = pendingPermissionEvent ?: return@registerForActivityResult
        pendingPermissionEvent = null
        if (isGranted) {
            captureViewModel?.handleEvent(event)
        }
    }

    private var captureViewModel: CaptureViewModel? = null

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
        pendingPermissionEvent = savedInstanceState?.getString(STATE_PENDING_EVENT)
            ?.toCaptureEventOrNull()
        val source = intent.getStringExtra(extraSource)
            ?.let(QuickItemSource::valueOf)
            ?: QuickItemSource.TILE
        setContent {
            val viewModel: CaptureViewModel = hiltViewModel()
            captureViewModel = viewModel
            QuickStackTheme {
                LaunchedEffect(source) {
                    viewModel.setSource(source)
                }
                CaptureScreen(
                    viewModel = viewModel,
                    onEvent = ::dispatchEvent,
                    onDismiss = { message ->
                        if (!message.isNullOrBlank()) {
                            setResult(
                                Activity.RESULT_OK,
                                Intent().putExtra(EXTRA_SUCCESS_MESSAGE, message),
                            )
                        }
                        finish()
                    },
                )
            }
        }
    }

    private fun dispatchEvent(event: CaptureEvent) {
        if (event.requiresNotificationPermission() && !applicationContext.hasNotificationPermission()) {
            pendingPermissionEvent = event
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            return
        }
        captureViewModel?.handleEvent(event)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_PENDING_EVENT, pendingPermissionEvent?.toStateKey())
    }

    companion object {
        /** Distinguishes tile-driven capture from in-app capture without duplicating screens. */
        const val extraSource = "extra_source"
        const val EXTRA_SUCCESS_MESSAGE = "extra_success_message"
        private const val STATE_PENDING_EVENT = "state_pending_event"
    }
}

private fun CaptureEvent.requiresNotificationPermission(): Boolean = when (this) {
    is CaptureEvent.SaveNote -> pin
    is CaptureEvent.SaveClipboard -> pin
    CaptureEvent.ScheduleReminderInOneHour,
    CaptureEvent.ScheduleReminderTonight,
    CaptureEvent.StartTimerTenMinutes,
    -> true
    is CaptureEvent.NoteChanged -> false
}

private fun CaptureEvent.toStateKey(): String? = when (this) {
    CaptureEvent.ScheduleReminderInOneHour -> "reminder_one_hour"
    CaptureEvent.ScheduleReminderTonight -> "reminder_tonight"
    CaptureEvent.StartTimerTenMinutes -> "timer_ten"
    is CaptureEvent.SaveNote -> if (pin) "pin_note" else null
    is CaptureEvent.SaveClipboard -> if (pin) "pin_clipboard" else null
    is CaptureEvent.NoteChanged -> null
}

private fun String.toCaptureEventOrNull(): CaptureEvent? = when (this) {
    "reminder_one_hour" -> CaptureEvent.ScheduleReminderInOneHour
    "reminder_tonight" -> CaptureEvent.ScheduleReminderTonight
    "timer_ten" -> CaptureEvent.StartTimerTenMinutes
    "pin_note" -> CaptureEvent.SaveNote(pin = true)
    "pin_clipboard" -> CaptureEvent.SaveClipboard(pin = true)
    else -> null
}
