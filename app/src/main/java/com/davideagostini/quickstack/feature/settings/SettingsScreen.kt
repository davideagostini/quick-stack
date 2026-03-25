package com.davideagostini.quickstack.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davideagostini.quickstack.BuildConfig
import com.davideagostini.quickstack.R
import com.davideagostini.quickstack.feature.settings.components.SettingsGroupTitle
import com.davideagostini.quickstack.feature.settings.components.SettingsInfoCard
import com.davideagostini.quickstack.feature.settings.components.SettingsLinkCard
import com.davideagostini.quickstack.feature.settings.components.SettingsOptionRow
import com.davideagostini.quickstack.feature.settings.components.SettingsScaffold
import com.davideagostini.quickstack.feature.settings.components.SettingsSectionCard
import com.davideagostini.quickstack.feature.settings.components.SettingsSectionLabel
import com.davideagostini.quickstack.feature.settings.model.languageLabelForTag
import com.davideagostini.quickstack.feature.settings.model.languageOptions
import com.davideagostini.quickstack.feature.settings.model.reminderOffsetOptions
import com.davideagostini.quickstack.feature.settings.model.timerOffsetOptions
import com.davideagostini.quickstack.feature.settings.model.tonightHourOptions

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    openLanguageSettings: () -> Unit,
    openTimeActionsSettings: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScaffold(
        title = { Text(stringResource(R.string.settings_title)) },
        navigationIconContentDescription = stringResource(R.string.content_desc_back),
        onBack = onBack,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp),
                )
            }
            item {
                SettingsSectionLabel(stringResource(R.string.settings_title))
            }
            item {
                SettingsLinkCard(
                    index = 0,
                    count = 2,
                    title = stringResource(R.string.settings_language_title),
                    subtitle = uiState.settings.languageTag?.let(::languageLabelForTag)
                        ?: stringResource(R.string.settings_language_system),
                    icon = Icons.Outlined.Language,
                    onClick = openLanguageSettings,
                )
            }
            item {
                SettingsLinkCard(
                    index = 1,
                    count = 2,
                    title = stringResource(R.string.settings_time_actions_title),
                    subtitle = stringResource(
                        R.string.settings_time_actions_summary,
                        uiState.settings.reminderOffsetMinutes,
                        uiState.settings.tonightHour,
                        uiState.settings.timerOffsetMinutes,
                    ),
                    icon = Icons.Outlined.Schedule,
                    onClick = openTimeActionsSettings,
                )
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                )
            }
            item {
                SettingsSectionLabel(stringResource(R.string.settings_about_title))
            }
            item {
                SettingsInfoCard(
                    index = 0,
                    count = 1,
                    title = stringResource(R.string.settings_version_title),
                    value = BuildConfig.VERSION_NAME,
                    icon = Icons.Outlined.Info,
                )
            }
        }
    }
}

@Composable
fun LanguageSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val languageOptions = languageOptions(
        systemLabel = stringResource(R.string.settings_language_system),
    )

    SettingsScaffold(
        title = { Text(stringResource(R.string.settings_language_title)) },
        navigationIconContentDescription = stringResource(R.string.content_desc_back),
        onBack = onBack,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SettingsSectionCard(
                    title = stringResource(R.string.settings_language_title),
                    subtitle = stringResource(R.string.settings_language_subtitle),
                    icon = Icons.Outlined.Language,
                ) {
                    languageOptions.forEach { option ->
                        SettingsOptionRow(
                            title = option.label,
                            selected = uiState.settings.languageTag == option.tag,
                            onClick = { viewModel.updateLanguage(option.tag) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimeActionsSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val reminderOptions = reminderOffsetOptions()
    val tonightOptions = tonightHourOptions()
    val timerOptions = timerOffsetOptions()

    LaunchedEffect(viewModel) {
        viewModel.messages.collect(snackbarHostState::showSnackbar)
    }

    SettingsScaffold(
        title = { Text(stringResource(R.string.settings_time_actions_title)) },
        navigationIconContentDescription = stringResource(R.string.content_desc_back),
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                SettingsSectionCard(
                    title = stringResource(R.string.settings_time_actions_title),
                    subtitle = stringResource(R.string.settings_time_actions_subtitle),
                    icon = Icons.Outlined.Schedule,
                ) {
                    SettingsGroupTitle(stringResource(R.string.settings_in_one_hour_title))
                    reminderOptions.forEach { option ->
                        SettingsOptionRow(
                            title = option.label,
                            selected = uiState.settings.reminderOffsetMinutes == option.value,
                            onClick = { viewModel.updateReminderOffset(option.value) },
                        )
                    }
                    SettingsGroupTitle(stringResource(R.string.settings_tonight_title))
                    tonightOptions.forEach { option ->
                        SettingsOptionRow(
                            title = option.label,
                            selected = uiState.settings.tonightHour == option.value,
                            onClick = { viewModel.updateTonightHour(option.value) },
                        )
                    }
                    SettingsGroupTitle(stringResource(R.string.settings_ten_min_title))
                    timerOptions.forEach { option ->
                        SettingsOptionRow(
                            title = option.label,
                            selected = uiState.settings.timerOffsetMinutes == option.value,
                            onClick = { viewModel.updateTimerOffset(option.value) },
                        )
                    }
                }
            }
        }
    }
}
