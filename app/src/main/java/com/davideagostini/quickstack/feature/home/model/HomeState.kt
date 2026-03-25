package com.davideagostini.quickstack.feature.home.model

import com.davideagostini.quickstack.domain.model.QuickItem

// Immutable screen state for the inbox/history screen.
data class HomeState(
    val items: List<QuickItem> = emptyList(),
)
