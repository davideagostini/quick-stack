package com.davideagostini.quickstack.feature.capture.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davideagostini.quickstack.R
import com.davideagostini.quickstack.core.ui.QuickStackButtonDefaults
import com.davideagostini.quickstack.core.ui.QuickStackButtonShape
import com.davideagostini.quickstack.core.ui.QuickStackSheetShape

@Composable
fun CaptureSectionCard(
    title: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = QuickStackSheetShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp),
                    ) {
                        androidx.compose.foundation.layout.Box(contentAlignment = Alignment.Center) {
                            icon()
                        }
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                content()
            },
        )
    }
}

@Composable
fun NoteActions(
    noteText: String,
    isWorking: Boolean,
    onNoteChange: (String) -> Unit,
    onSaveNote: (Boolean) -> Unit,
) {
    androidx.compose.material3.OutlinedTextField(
        value = noteText,
        onValueChange = onNoteChange,
        modifier = Modifier.fillMaxWidth(),
        minLines = 5,
        enabled = !isWorking,
        label = { Text(text = stringResource(R.string.capture_note_placeholder)) },
        shape = QuickStackButtonShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = QuickStackButtonDefaults.neutralOutlinedBorderColor(),
            unfocusedBorderColor = QuickStackButtonDefaults.neutralOutlinedBorderColor(),
            disabledBorderColor = QuickStackButtonDefaults.neutralOutlinedBorderColor(),
        ),
    )
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = { onSaveNote(false) },
            enabled = !isWorking,
            modifier = Modifier.fillMaxWidth(),
            shape = QuickStackButtonShape,
            colors = QuickStackButtonDefaults.primaryButtonColors(),
        ) {
            Icon(
                imageVector = Icons.Outlined.Save,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.capture_save_note),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        OutlinedButton(
            onClick = { onSaveNote(true) },
            enabled = !isWorking,
            modifier = Modifier.fillMaxWidth(),
            shape = QuickStackButtonShape,
            colors = QuickStackButtonDefaults.neutralOutlinedButtonColors(),
            border = QuickStackButtonDefaults.neutralOutlinedBorder(),
        ) {
            Icon(
                imageVector = Icons.Outlined.PushPin,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.capture_pin_note),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
fun ClipboardActions(
    isWorking: Boolean,
    onSaveClipboard: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = { onSaveClipboard(false) },
            enabled = !isWorking,
            modifier = Modifier.fillMaxWidth(),
            shape = QuickStackButtonShape,
            colors = QuickStackButtonDefaults.primaryButtonColors(),
        ) {
            Icon(
                imageVector = Icons.Outlined.Save,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.capture_save_clipboard),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
        OutlinedButton(
            onClick = { onSaveClipboard(true) },
            enabled = !isWorking,
            modifier = Modifier.fillMaxWidth(),
            shape = QuickStackButtonShape,
            colors = QuickStackButtonDefaults.neutralOutlinedButtonColors(),
            border = QuickStackButtonDefaults.neutralOutlinedBorder(),
        ) {
            Icon(
                imageVector = Icons.Outlined.PushPin,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = stringResource(R.string.capture_pin_clipboard),
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Composable
fun ReminderActions(
    isWorking: Boolean,
    reminderLabel: String,
    tonightLabel: String,
    timerLabel: String,
    onReminderInOneHour: () -> Unit,
    onReminderTonight: () -> Unit,
    onTimerTenMinutes: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onReminderInOneHour,
                enabled = !isWorking,
                modifier = Modifier.weight(1f),
                shape = QuickStackButtonShape,
                colors = QuickStackButtonDefaults.neutralOutlinedButtonColors(),
                border = QuickStackButtonDefaults.neutralOutlinedBorder(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = reminderLabel,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
            OutlinedButton(
                onClick = onReminderTonight,
                enabled = !isWorking,
                modifier = Modifier.weight(1f),
                shape = QuickStackButtonShape,
                colors = QuickStackButtonDefaults.neutralOutlinedButtonColors(),
                border = QuickStackButtonDefaults.neutralOutlinedBorder(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Text(
                    text = tonightLabel,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
        TimerAction(
            isWorking = isWorking,
            timerLabel = timerLabel,
            onTimerTenMinutes = onTimerTenMinutes,
        )
    }
}

@Composable
fun TimerAction(
    isWorking: Boolean,
    timerLabel: String,
    onTimerTenMinutes: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = QuickStackSheetShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        OutlinedButton(
            onClick = onTimerTenMinutes,
            enabled = !isWorking,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            shape = QuickStackButtonShape,
            colors = QuickStackButtonDefaults.tertiaryOutlinedButtonColors(),
            border = QuickStackButtonDefaults.neutralOutlinedBorder(),
        ) {
            Icon(
                imageVector = Icons.Outlined.Timer,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = timerLabel,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}
