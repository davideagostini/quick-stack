package com.davideagostini.quickstack.feature.capture

import androidx.lifecycle.viewModelScope
import com.davideagostini.quickstack.core.viewmodel.BaseViewModel
import com.davideagostini.quickstack.data.repository.ClipboardRepository
import com.davideagostini.quickstack.data.repository.QuickItemRepository
import com.davideagostini.quickstack.data.repository.QuickStackSettingsRepository
import com.davideagostini.quickstack.domain.model.QuickItemDraft
import com.davideagostini.quickstack.domain.model.QuickItemSource
import com.davideagostini.quickstack.domain.model.QuickItemType
import com.davideagostini.quickstack.feature.capture.model.CaptureEvent
import com.davideagostini.quickstack.feature.capture.model.CaptureState
import com.davideagostini.quickstack.feature.notifications.QuickStackNotificationManager
import com.davideagostini.quickstack.feature.reminders.ReminderScheduleCalculator
import com.davideagostini.quickstack.feature.reminders.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the lightweight capture flow.
 *
 * It accepts simple UI intents, validates the minimum required input, persists the item,
 * and optionally publishes a pinned notification before asking the Activity to close.
 */
@HiltViewModel
class CaptureViewModel @Inject constructor(
    private val quickItemRepository: QuickItemRepository,
    private val clipboardRepository: ClipboardRepository,
    private val notificationManager: QuickStackNotificationManager,
    private val reminderScheduler: ReminderScheduler,
    private val settingsRepository: QuickStackSettingsRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(CaptureState())
    val uiState: StateFlow<CaptureState> = _uiState.asStateFlow()
    private var source: QuickItemSource = QuickItemSource.APP

    private val _closeRequests = MutableSharedFlow<String?>()
    val closeRequests = _closeRequests.asSharedFlow()

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                _uiState.update {
                    it.copy(
                        reminderOffsetMinutes = settings.reminderOffsetMinutes,
                        tonightHour = settings.tonightHour,
                        tonightMinute = settings.tonightMinute,
                        timerOffsetMinutes = settings.timerOffsetMinutes,
                    )
                }
            }
        }
    }

    fun setSource(source: QuickItemSource) {
        this.source = source
    }

    fun handleEvent(event: CaptureEvent) {
        if (_uiState.value.isWorking) return
        when (event) {
            is CaptureEvent.NoteChanged -> _uiState.update { it.copy(noteText = event.value) }
            is CaptureEvent.SaveNote -> saveNote(pin = event.pin)
            is CaptureEvent.SaveClipboard -> saveClipboard(pin = event.pin)
            CaptureEvent.ScheduleReminderInOneHour -> scheduleReminderInOneHour()
            CaptureEvent.ScheduleReminderTonight -> scheduleReminderTonight()
            CaptureEvent.StartTimerTenMinutes -> startTimerTenMinutes()
        }
    }

    private fun saveNote(pin: Boolean) {
        val text = uiState.value.noteText.trim()
        if (text.isBlank()) {
            emitMessage("Enter a note first.")
            return
        }
        createItem(
            draft = QuickItemDraft(
                type = QuickItemType.TEXT_NOTE,
                content = text,
                isPinned = pin,
                source = source,
            ),
            clearText = true,
        )
    }

    private fun saveClipboard(pin: Boolean) {
        val text = clipboardRepository.readLatestText()
        if (text.isNullOrBlank()) {
            emitMessage("Clipboard is empty or does not contain text.")
            return
        }
        createItem(
            draft = QuickItemDraft(
                type = QuickItemType.CLIPBOARD,
                content = text,
                isPinned = pin,
                source = source,
            ),
            clearText = false,
        )
    }

    private fun scheduleReminderInOneHour() {
        createScheduledItem(
            QuickItemDraft(
                type = QuickItemType.REMINDER,
                content = "In ${uiState.value.reminderOffsetMinutes} min",
                isPinned = false,
                scheduledAt = ReminderScheduleCalculator.oneHourFromNow(
                    minutesFromNow = uiState.value.reminderOffsetMinutes,
                ),
                source = source,
            ),
        )
    }

    private fun scheduleReminderTonight() {
        createScheduledItem(
            QuickItemDraft(
                type = QuickItemType.REMINDER,
                content = "Tonight",
                isPinned = false,
                scheduledAt = ReminderScheduleCalculator.tonight(
                    hour = uiState.value.tonightHour,
                    minute = uiState.value.tonightMinute,
                ),
                source = source,
            ),
        )
    }

    private fun startTimerTenMinutes() {
        createScheduledItem(
            QuickItemDraft(
                type = QuickItemType.TIMER,
                content = "${uiState.value.timerOffsetMinutes} minutes",
                isPinned = false,
                scheduledAt = ReminderScheduleCalculator.tenMinutesFromNow(
                    minutesFromNow = uiState.value.timerOffsetMinutes,
                ),
                source = source,
            ),
        )
    }

    private fun createItem(draft: QuickItemDraft, clearText: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true) }
            val item = quickItemRepository.createItem(draft)
            if (item.isPinned) {
                notificationManager.showPinnedItem(item)
            }
            if (clearText) {
                _uiState.update { it.copy(noteText = "", isWorking = false) }
            } else {
                _uiState.update { it.copy(isWorking = false) }
            }
            _closeRequests.emit(successMessage(item.type, item.isPinned))
        }
    }

    private fun createScheduledItem(draft: QuickItemDraft) {
        viewModelScope.launch {
            _uiState.update { it.copy(isWorking = true) }
            val item = quickItemRepository.createItem(draft)
            val scheduled = reminderScheduler.schedule(item)
            if (!scheduled) {
                quickItemRepository.deleteItem(item.id)
                _uiState.update { it.copy(isWorking = false) }
                emitMessage("Unable to schedule this right now.")
                return@launch
            }
            _uiState.update { it.copy(noteText = "", isWorking = false) }
            _closeRequests.emit(successMessage(item.type, item.isPinned))
        }
    }

    private fun successMessage(type: QuickItemType, pinned: Boolean): String = when {
        type == QuickItemType.TEXT_NOTE && pinned -> "Pinned note added."
        type == QuickItemType.TEXT_NOTE -> "Note added."
        type == QuickItemType.CLIPBOARD && pinned -> "Pinned clipboard added."
        type == QuickItemType.CLIPBOARD -> "Clipboard saved."
        type == QuickItemType.REMINDER -> "Reminder added."
        else -> "${uiState.value.timerOffsetMinutes} min reminder added."
    }
}
