package com.davideagostini.quickstack.feature.capture.model

// Minimal state for the full-screen quick capture flow.
data class CaptureState(
    val noteText: String = "",
    val isWorking: Boolean = false,
    val reminderOffsetMinutes: Int = 60,
    val tonightHour: Int = 20,
    val tonightMinute: Int = 0,
    val timerOffsetMinutes: Int = 10,
)
