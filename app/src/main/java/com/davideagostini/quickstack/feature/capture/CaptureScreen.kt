package com.davideagostini.quickstack.feature.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentPaste
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davideagostini.quickstack.R
import com.davideagostini.quickstack.core.ui.QuickStackColors
import com.davideagostini.quickstack.feature.capture.components.CaptureSectionCard
import com.davideagostini.quickstack.feature.capture.components.ClipboardActions
import com.davideagostini.quickstack.feature.capture.components.NoteActions
import com.davideagostini.quickstack.feature.capture.components.ReminderActions
import com.davideagostini.quickstack.feature.capture.model.CaptureEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureScreen(
    viewModel: CaptureViewModel,
    onEvent: (CaptureEvent) -> Unit,
    onDismiss: (String?) -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val reminderLabel = when {
        state.reminderOffsetMinutes == 60 -> stringResource(R.string.capture_reminder_one_hour)
        state.reminderOffsetMinutes % 60 == 0 ->
            stringResource(R.string.settings_in_hours_format, state.reminderOffsetMinutes / 60)
        else -> stringResource(R.string.settings_in_minutes_format, state.reminderOffsetMinutes)
    }
    val tonightLabel = stringResource(
        R.string.settings_tonight_at_format,
        state.tonightHour,
        state.tonightMinute,
    )
    val timerLabel = stringResource(R.string.settings_remind_me_in_minutes_format, state.timerOffsetMinutes)

    LaunchedEffect(viewModel) {
        viewModel.messages.collect(snackbarHostState::showSnackbar)
    }

    LaunchedEffect(viewModel) {
        viewModel.closeRequests.collect { message ->
            onDismiss(message)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = QuickStackColors.topBarColors,
                title = { Text(text = stringResource(R.string.capture_nav_title)) },
                navigationIcon = {
                    IconButton(onClick = { onDismiss(null) }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = stringResource(R.string.content_desc_close),
                        )
                    }
                },
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.navigationBarsPadding(),
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                HeroSection()
            }
            item {
                CaptureSectionCard(
                    title = stringResource(R.string.capture_note_section_title),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.EditNote,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    },
                ) {
                    NoteActions(
                        noteText = state.noteText,
                        isWorking = state.isWorking,
                        onNoteChange = { onEvent(CaptureEvent.NoteChanged(it)) },
                        onSaveNote = { onEvent(CaptureEvent.SaveNote(it)) },
                    )
                }
            }
            item {
                CaptureSectionCard(
                    title = stringResource(R.string.capture_clipboard_section_title),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.ContentPaste,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    },
                ) {
                    ClipboardActions(
                        isWorking = state.isWorking,
                        onSaveClipboard = { onEvent(CaptureEvent.SaveClipboard(it)) },
                    )
                }
            }
            item {
                CaptureSectionCard(
                    title = stringResource(R.string.capture_time_section_title),
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    },
                ) {
                    ReminderActions(
                        isWorking = state.isWorking,
                        reminderLabel = reminderLabel,
                        tonightLabel = tonightLabel,
                        timerLabel = timerLabel,
                        onReminderInOneHour = { onEvent(CaptureEvent.ScheduleReminderInOneHour) },
                        onReminderTonight = { onEvent(CaptureEvent.ScheduleReminderTonight) },
                        onTimerTenMinutes = { onEvent(CaptureEvent.StartTimerTenMinutes) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSection() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = stringResource(R.string.capture_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
