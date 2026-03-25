package com.davideagostini.quickstack.feature.home.model

import com.davideagostini.quickstack.domain.model.QuickItem

// Intents coming from the UI. The ViewModel consumes these and performs the actual work.
sealed class HomeEvent {
    data class Delete(val item: QuickItem) : HomeEvent()
    data class DismissPinned(val item: QuickItem) : HomeEvent()
    data class CompletePinned(val item: QuickItem) : HomeEvent()
    data class DismissTriggered(val item: QuickItem) : HomeEvent()
    data class CompleteTriggered(val item: QuickItem) : HomeEvent()
}
