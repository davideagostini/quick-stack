package com.davideagostini.quickstack.feature.settings

import android.content.Context
import com.davideagostini.quickstack.R
import androidx.lifecycle.viewModelScope
import com.davideagostini.quickstack.core.applyAppLanguage
import com.davideagostini.quickstack.core.viewmodel.BaseViewModel
import com.davideagostini.quickstack.data.repository.QuickStackSettingsRepository
import com.davideagostini.quickstack.feature.settings.model.SettingsState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val settingsRepository: QuickStackSettingsRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update { it.copy(settings = settings) }
            }
        }
    }

    fun updateLanguage(languageTag: String?) {
        settingsRepository.updateLanguage(languageTag)
        applyAppLanguage(languageTag)
    }

    fun updateReminderOffset(minutes: Int) {
        settingsRepository.updateReminderOffset(minutes)
        emitMessage(context.getString(R.string.message_time_actions_updated))
    }

    fun updateTonightHour(hour: Int) {
        settingsRepository.updateTonightTime(hour)
        emitMessage(context.getString(R.string.message_time_actions_updated))
    }

    fun updateTimerOffset(minutes: Int) {
        settingsRepository.updateTimerOffset(minutes)
        emitMessage(context.getString(R.string.message_time_actions_updated))
    }
}
