package com.davideagostini.quickstack.feature.home

import androidx.lifecycle.viewModelScope
import com.davideagostini.quickstack.core.viewmodel.BaseViewModel
import com.davideagostini.quickstack.data.repository.QuickItemRepository
import com.davideagostini.quickstack.feature.home.model.HomeEvent
import com.davideagostini.quickstack.feature.home.model.HomeState
import com.davideagostini.quickstack.feature.notifications.QuickStackNotificationManager
import com.davideagostini.quickstack.feature.reminders.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the inbox/history screen.
 *
 * The repository flow is the source of truth; UI events trigger side effects and the next
 * rendered state arrives through Room observation instead of manual list mutation.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val quickItemRepository: QuickItemRepository,
    private val notificationManager: QuickStackNotificationManager,
    private val reminderScheduler: ReminderScheduler,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            quickItemRepository.observeItems().collect { items ->
                _uiState.update { it.copy(items = items) }
            }
        }
    }

    fun handleEvent(event: HomeEvent) {
        viewModelScope.launch {
            when (event) {
                is HomeEvent.Delete -> {
                    if (event.item.isPendingSchedule) {
                        reminderScheduler.cancel(event.item.id)
                    }
                    quickItemRepository.deleteItem(event.item.id)
                    notificationManager.cancelItemNotification(event.item.id)
                    emitMessage("Item deleted.")
                }
                is HomeEvent.DismissPinned -> {
                    quickItemRepository.dismissPinned(event.item.id)
                    notificationManager.cancelItemNotification(event.item.id)
                }
                is HomeEvent.CompletePinned -> {
                    quickItemRepository.completePinned(event.item.id)
                    notificationManager.cancelItemNotification(event.item.id)
                }
                is HomeEvent.DismissTriggered -> {
                    quickItemRepository.resolveTriggered(event.item.id)
                    notificationManager.cancelItemNotification(event.item.id)
                }
                is HomeEvent.CompleteTriggered -> {
                    quickItemRepository.resolveTriggered(event.item.id)
                    notificationManager.cancelItemNotification(event.item.id)
                }
            }
        }
    }
}
