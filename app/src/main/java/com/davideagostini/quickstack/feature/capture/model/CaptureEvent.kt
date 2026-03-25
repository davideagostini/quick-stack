package com.davideagostini.quickstack.feature.capture.model

// UI intents for the capture flow. The ViewModel decides how to execute them.
sealed class CaptureEvent {
    data class NoteChanged(val value: String) : CaptureEvent()
    data class SaveNote(val pin: Boolean) : CaptureEvent()
    data class SaveClipboard(val pin: Boolean) : CaptureEvent()
    data object ScheduleReminderInOneHour : CaptureEvent()
    data object ScheduleReminderTonight : CaptureEvent()
    data object StartTimerTenMinutes : CaptureEvent()
}
