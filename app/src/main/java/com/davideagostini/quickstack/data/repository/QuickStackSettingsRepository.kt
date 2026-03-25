package com.davideagostini.quickstack.data.repository

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

data class QuickStackSettings(
    val languageTag: String? = null,
    val reminderOffsetMinutes: Int = 60,
    val tonightHour: Int = 20,
    val tonightMinute: Int = 0,
    val timerOffsetMinutes: Int = 10,
)

@Singleton
class QuickStackSettingsRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<QuickStackSettings> = _settings.asStateFlow()

    fun currentSettings(): QuickStackSettings = _settings.value

    fun updateLanguage(languageTag: String?) {
        prefs.edit().putString(KEY_LANGUAGE_TAG, languageTag).apply()
        _settings.update { it.copy(languageTag = languageTag) }
    }

    fun updateReminderOffset(minutes: Int) {
        prefs.edit().putInt(KEY_REMINDER_OFFSET, minutes).apply()
        _settings.update { it.copy(reminderOffsetMinutes = minutes) }
    }

    fun updateTonightTime(hour: Int, minute: Int = 0) {
        prefs.edit()
            .putInt(KEY_TONIGHT_HOUR, hour)
            .putInt(KEY_TONIGHT_MINUTE, minute)
            .apply()
        _settings.update { it.copy(tonightHour = hour, tonightMinute = minute) }
    }

    fun updateTimerOffset(minutes: Int) {
        prefs.edit().putInt(KEY_TIMER_OFFSET, minutes).apply()
        _settings.update { it.copy(timerOffsetMinutes = minutes) }
    }

    private fun loadSettings(): QuickStackSettings = QuickStackSettings(
        languageTag = prefs.getString(KEY_LANGUAGE_TAG, null),
        reminderOffsetMinutes = prefs.getInt(KEY_REMINDER_OFFSET, 60),
        tonightHour = prefs.getInt(KEY_TONIGHT_HOUR, 20),
        tonightMinute = prefs.getInt(KEY_TONIGHT_MINUTE, 0),
        timerOffsetMinutes = prefs.getInt(KEY_TIMER_OFFSET, 10),
    )

    companion object {
        private const val PREFS_NAME = "quickstack_settings"
        private const val KEY_LANGUAGE_TAG = "language_tag"
        private const val KEY_REMINDER_OFFSET = "reminder_offset_minutes"
        private const val KEY_TONIGHT_HOUR = "tonight_hour"
        private const val KEY_TONIGHT_MINUTE = "tonight_minute"
        private const val KEY_TIMER_OFFSET = "timer_offset_minutes"
    }
}
