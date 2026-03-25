package com.davideagostini.quickstack.feature.settings.model

import com.davideagostini.quickstack.data.repository.QuickStackSettings

data class SettingsState(
    val settings: QuickStackSettings = QuickStackSettings(),
)
