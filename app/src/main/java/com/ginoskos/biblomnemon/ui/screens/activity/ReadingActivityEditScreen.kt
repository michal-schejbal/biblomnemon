package com.ginoskos.biblomnemon.ui.screens.activity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.core.koinSharedViewModel
import com.ginoskos.biblomnemon.data.entities.ReadingActivity
import com.ginoskos.biblomnemon.ui.navigation.ScreenToolbar
import com.ginoskos.biblomnemon.ui.navigation.navigateBack
import com.ginoskos.biblomnemon.ui.screens.SubScreen
import com.ginoskos.biblomnemon.ui.screens.common.BookSelectorField
import com.ginoskos.biblomnemon.ui.screens.common.ClearableOutlinedTextField
import com.ginoskos.biblomnemon.ui.screens.common.DateTimePickerField
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun ReadingActivityEditScreen(navController: NavHostController) {
    val model: ReadingActivityEditViewModel = koinViewModel()
    val transfer: ReadingActivityTransferViewModel = koinSharedViewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        transfer.pop { item ->
            model.fetch(item = item)
        }
    }

    SubScreen(
        navController = navController,
        topBar = {
            ScreenToolbar(onBack = {
                navController.navigateBack()
            }) {
                ReadingActivityEditTopBarActions(
                    isSave = uiState.item.title?.isNotBlank() ?: false,
                    isEdit = uiState.isStored,
                    onSaveClick = {
                        model.store()
                        navController.navigateBack()
                    },
                    onDeleteClick = {
                        if (uiState.isStored) {
                            model.delete(uiState.item)
                        }
                        navController.navigateBack()
                    }
                )
            }
        }
    ) {
        ReadingActivityEditScreenContent(
            uiState = uiState,
            onUpdate = model::update
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadingActivityEditScreenContent(
    uiState: ReadingActivityEditUiState,
    onUpdate: (ReadingActivity) -> Unit = {}
) {
    val scroll = rememberScrollState()

    if (uiState.loading) {
        LoadingComponent(modifier = Modifier.fillMaxWidth())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        with(uiState.item) {
            // Title
            ClearableOutlinedTextField(
                value = title.orEmpty(),
                onValueChange = { onUpdate(copy(title = it)) },
                label = stringResource(R.string.reading_activity_title)
            )

            // Description
            ClearableOutlinedTextField(
                value = description ?: "",
                onValueChange = { onUpdate(copy(description = it)) },
                label = stringResource(R.string.reading_activity_description),
                singleLine = false
            )

            // Book Selection
            BookSelectorField(
                book = book,
                onBookSelected = { onUpdate(copy(book = it)) }
            )

            // Pages Read
            ClearableOutlinedTextField(
                value = pagesRead?.toString() ?: "",
                onValueChange = {
                    val clean = it.filter { ch -> ch.isDigit() }
                    onUpdate(copy(pagesRead = clean.toIntOrNull()))
                },
                label = stringResource(R.string.reading_activity_pages_read_label),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Started Date & Time
            DateTimePickerField(
                label = stringResource(R.string.reading_activity_started),
                timestamp = started,
                onTimestampChange = { timestamp -> 
                    timestamp?.let { onUpdate(copy(started = it)) }
                }
            )

            // Ended Date & Time (optional)
            DateTimePickerField(
                label = stringResource(R.string.reading_activity_ended),
                timestamp = ended,
                onTimestampChange = { timestamp -> 
                    onUpdate(copy(ended = timestamp))
                },
                isOptional = true
            )
        }
    }
}

@Composable
private fun ReadingActivityEditTopBarActions(
    isSave: Boolean,
    isEdit: Boolean,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Row {
        TextButton(
            onClick = onSaveClick,
            enabled = isSave
        ) {
            Text(stringResource(R.string.action_save).uppercase())
        }

        if (isEdit) {
            IconButton(onClick = { showMenu = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.action_more))
            }

            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.action_delete)) },
                    onClick = {
                        showMenu = false
                        showDeleteDialog = true
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, contentDescription = null)
                    }
                )
            }
        }
    }

    if (isEdit) {
        ConfirmDeleteDialog(
            show = showDeleteDialog,
            onConfirm = onDeleteClick,
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
fun ConfirmDeleteDialog(
    show: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(R.string.reading_activity_delete_title)) },
            text = { Text(stringResource(R.string.reading_activity_delete_message)) },
            confirmButton = {
                TextButton(onClick = onConfirm) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReadingActivityEditScreenContentPreview() {
    BiblomnemonTheme {
        ReadingActivityEditScreenContent(
            uiState = ReadingActivityEditUiState()
        )
    }
}
