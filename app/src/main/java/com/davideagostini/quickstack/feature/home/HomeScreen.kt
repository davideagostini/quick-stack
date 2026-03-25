package com.davideagostini.quickstack.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davideagostini.quickstack.R
import com.davideagostini.quickstack.core.ui.QuickStackButtonShape
import com.davideagostini.quickstack.core.ui.QuickStackButtonDefaults
import com.davideagostini.quickstack.core.ui.QuickStackColors
import com.davideagostini.quickstack.core.ui.QuickStackSheetShape
import com.davideagostini.quickstack.core.ui.components.DeleteConfirmationDialog
import com.davideagostini.quickstack.domain.model.QuickItem
import com.davideagostini.quickstack.feature.home.components.quickItemDisplayTitle
import com.davideagostini.quickstack.feature.home.components.QuickItemMetaText
import com.davideagostini.quickstack.feature.home.components.quickItemIcon
import com.davideagostini.quickstack.feature.home.components.QuickItemRow
import com.davideagostini.quickstack.feature.home.model.HomeEvent

/**
 * Inbox/history screen for persisted quick items.
 *
 * This screen is intentionally simple: it shows the latest items and exposes the minimum
 * actions needed to keep the list clean or resolve pinned entries.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    openCapture: () -> Unit,
    openSettings: () -> Unit,
    externalMessage: String?,
    onExternalMessageShown: () -> Unit,
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedItem by remember { mutableStateOf<QuickItem?>(null) }
    var pendingDeleteItem by remember { mutableStateOf<QuickItem?>(null) }

    LaunchedEffect(viewModel) {
        viewModel.messages.collect(snackbarHostState::showSnackbar)
    }

    LaunchedEffect(externalMessage) {
        val message = externalMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        onExternalMessageShown()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = QuickStackColors.topBarColors,
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(text = stringResource(R.string.app_name))
                        Text(
                            text = stringResource(R.string.tile_subtitle),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = openSettings) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.settings_title),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = openCapture,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = stringResource(R.string.capture_open),
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->
        if (uiState.items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = QuickStackSheetShape,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.capture_title),
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        Text(
                            text = stringResource(R.string.home_empty_state),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
            ) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp),
                    )
                }
                itemsIndexed(items = uiState.items, key = { _, item -> item.id }) { index, item ->
                    QuickItemRow(
                        item = item,
                        index = index,
                        count = uiState.items.size,
                        onClick = { selectedItem = item },
                    )
                }
            }
        }

        selectedItem?.let { item ->
            ModalBottomSheet(
                onDismissRequest = { selectedItem = null },
                sheetState = sheetState,
                containerColor = Color.Transparent,
                dragHandle = null,
                scrimColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.32f),
            ) {
                QuickItemActionSheet(
                    item = item,
                    onDelete = {
                        pendingDeleteItem = item
                        selectedItem = null
                    },
                    onDismiss = {
                        if (item.isTriggeredActionable) {
                            viewModel.handleEvent(HomeEvent.DismissTriggered(item))
                        } else {
                            viewModel.handleEvent(HomeEvent.DismissPinned(item))
                        }
                        selectedItem = null
                    },
                    onComplete = {
                        if (item.isTriggeredActionable) {
                            viewModel.handleEvent(HomeEvent.CompleteTriggered(item))
                        } else {
                            viewModel.handleEvent(HomeEvent.CompletePinned(item))
                        }
                        selectedItem = null
                    },
                    onClose = { selectedItem = null },
                )
            }
        }

        pendingDeleteItem?.let { item ->
            DeleteConfirmationDialog(
                title = stringResource(R.string.delete_item_title),
                message = stringResource(R.string.delete_item_message),
                onConfirm = {
                    viewModel.handleEvent(HomeEvent.Delete(item))
                    pendingDeleteItem = null
                },
                onDismiss = { pendingDeleteItem = null },
            )
        }
    }
}

@Composable
private fun QuickItemActionSheet(
    item: QuickItem,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    onComplete: () -> Unit,
    onClose: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = QuickStackSheetShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = stringResource(R.string.content_desc_close),
                    )
                }
            }

            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = quickItemIcon(item),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Text(
                text = quickItemDisplayTitle(item),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            Text(
                text = item.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            QuickItemMetaText(
                item = item,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )

            if (item.isActivePinned || item.isTriggeredActionable) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = QuickStackButtonShape,
                        colors = QuickStackButtonDefaults.neutralOutlinedButtonColors(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(text = stringResource(R.string.action_dismiss))
                    }

                    Button(
                        onClick = onComplete,
                        shape = QuickStackButtonShape,
                        colors = QuickStackButtonDefaults.primaryButtonColors(),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DoneAll,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(text = stringResource(R.string.action_complete))
                    }
                }
            }

            OutlinedButton(
                onClick = onDelete,
                shape = QuickStackButtonShape,
                colors = QuickStackButtonDefaults.destructiveOutlinedButtonColors(),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(text = stringResource(R.string.action_delete))
            }
        }
    }
}
