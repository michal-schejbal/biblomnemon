package com.ginoskos.biblomnemon.ui.screens.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.data.entities.Category
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.ThemeLayout
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import com.ginoskos.biblomnemon.ui.theme.components.MessageComponent
import kotlinx.coroutines.flow.drop
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerBottomSheet(
    bookId: String? = null,
    onDismiss: (List<Category>) -> Unit
) {
    val model: CategoryManagerViewModel = koinViewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val dialogState by model.dialog.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(bookId) {
        model.fetch(bookId)
    }

    LaunchedEffect(model) {
        model.events.collect { event ->
            when (event) {
                is CategoryManagerUiEvent.Success -> {
                    snackbarHostState.showSnackbar(context.getString(event.message))
                }
                is CategoryManagerUiEvent.Failure -> {
                    snackbarHostState.showSnackbar(context.getString(event.message))
                }

                CategoryManagerUiEvent.Dismiss -> {
                    model.setDialogState(CategoryDialogState.None)
                }

                // Creation
                CategoryManagerUiEvent.CreateDialog -> {
                    model.setDialogState(CategoryDialogState.Create)
                }
                is CategoryManagerUiEvent.Create -> {
                    model.create(event.title)
                }

                // Renaming
                is CategoryManagerUiEvent.RenameDialog -> {
                    model.setDialogState(CategoryDialogState.Rename(event.item))
                }
                is CategoryManagerUiEvent.Rename -> {
                    model.rename(event.item, event.title)
                }

                // Deletion
                is CategoryManagerUiEvent.DeleteDialog -> {
                    model.setDialogState(CategoryDialogState.Delete(event.item))
                }
                is CategoryManagerUiEvent.Delete -> {
                    model.delete(event.item)
                }

                // Selection
                is CategoryManagerUiEvent.Select -> {
                    model.select(event.item, bookId)
                }
            }
        }
    }

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.currentValue }
            .drop(1)
            .collect { value ->
                if (value == SheetValue.Hidden) {
                    onDismiss(emptyList())
                }
            }
    }

    ModalBottomSheet(
        onDismissRequest = {
            onDismiss(emptyList())
        },
        sheetState = sheetState,
        modifier = Modifier
            .navigationBarsPadding()
            .imePadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
        ) {
            CategoryManagerContent(
                uiState = uiState,
                onAction = model::onEvent,
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }

    CategoryDialogContent(
        uiState = dialogState,
        onAction = model::onEvent
    )
}

@Composable
fun CategoryManagerContent(
    uiState: CategoryManagerUiState,
    onAction: (CategoryManagerUiEvent) -> Unit = {},
    onQueryChange: (String) -> Unit = {}
) {
    when (uiState) {
        is CategoryManagerUiState.Loading -> {
            LoadingComponent()
        }

        is CategoryManagerUiState.Error -> {
            MessageComponent(
                message = stringResource(id = R.string.search_error_message, uiState.message),
                iconVector = Icons.Default.Clear
            )
        }

        is CategoryManagerUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ThemeLayout.offset),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = onQueryChange,
                    label = { Text(stringResource(R.string.library_edit_categories_search_placeholder)) },
                    trailingIcon = {
                        IconButton(onClick = { onAction(CategoryManagerUiEvent.CreateDialog) }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.library_edit_categories_create_button))
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.items.isEmpty()) {
                    Spacer(modifier = Modifier.height(40.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MessageComponent(modifier = Modifier, message = stringResource(R.string.library_edit_categories_empty))

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(onClick = { onAction(CategoryManagerUiEvent.CreateDialog) }) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.library_edit_categories_create_button))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.library_edit_categories_create_button))
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(items = uiState.items, key = { it.id!! }) { category ->
                            CategoryListItem(
                                category = category,
                                isSelected = uiState.selection.any { it.id == category.id },
                                onAction = onAction
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryListItem(
    category: Category,
    isSelected: Boolean,
    onAction: (CategoryManagerUiEvent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAction(CategoryManagerUiEvent.Select(category)) }
            .padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onAction(CategoryManagerUiEvent.Select(category)) }
            )
            Text(category.title.orEmpty())
        }

        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "More actions")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.library_edit_categories_rename_button)) },
                    leadingIcon = {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.library_edit_categories_rename_content_desc))
                    },
                    onClick = {
                        expanded = false
                        onAction(CategoryManagerUiEvent.RenameDialog(category))
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.library_edit_categories_delete_button)) },
                    leadingIcon = {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.library_edit_categories_delete_content_desc))
                    },
                    onClick = {
                        expanded = false
                        onAction(CategoryManagerUiEvent.DeleteDialog(category))
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryDialogContent(
    uiState: CategoryDialogState,
    onAction: (CategoryManagerUiEvent) -> Unit = {},
) {
    when (uiState) {
        is CategoryDialogState.Create -> {
            CategoryCreateDialog(
                onConfirm = { title ->
                    onAction(CategoryManagerUiEvent.Create(title))
                },
                onDismiss = {
                    onAction(CategoryManagerUiEvent.Dismiss)
                }
            )
        }

        is CategoryDialogState.Rename -> {
            CategoryRenameDialog(
                category = uiState.item,
                onConfirm = { title ->
                    onAction(CategoryManagerUiEvent.Rename(
                        uiState.item, title))
                },
                onDismiss = {
                    onAction(CategoryManagerUiEvent.Dismiss)
                }
            )
        }

        is CategoryDialogState.Delete -> {
            CategoryDeleteDialog(
                category = uiState.item,
                onConfirm = {
                    onAction(CategoryManagerUiEvent.Delete(
                        uiState.item))
                },
                onDismiss = {
                    onAction(CategoryManagerUiEvent.Dismiss)
                }
            )
        }

        CategoryDialogState.None -> Unit
    }
}


@Composable
private fun CategoryCreateDialog(
    onConfirm: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var title by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.library_edit_categories_create_title)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.library_edit_categories_create_title_label)) }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title) }, enabled = title.isNotBlank()) {
                Text(stringResource(R.string.library_edit_categories_create_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun CategoryRenameDialog(
    category: Category,
    onConfirm: (String) -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    var title by remember { mutableStateOf(category.title!!) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.library_edit_categories_rename_title)) },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.library_edit_categories_rename_new_title)) }
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(title) }, enabled = title.isNotBlank()) {
                Text(stringResource(R.string.library_edit_categories_rename_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
private fun CategoryDeleteDialog(
    category: Category,
    onConfirm: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.library_edit_categories_delete_title)) },
        text = { Text(stringResource(R.string.library_edit_categories_delete_message, category.title!!)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.library_edit_categories_delete_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}


@Preview(showBackground = true, name = "Category Manager Content")
@Composable
fun CategoryManagerContentPreview() {
    val sampleCategories = listOf(
        Category(id = 1, title = "Fiction"),
        Category(id = 2, title = "Philosophy"),
        Category(id = 3, title = "History"),
        Category(id = 4, title = "Science"),
    )
    val selected = remember { mutableStateOf(listOf(Category(id = 2, title = "Philosophy"))) }
    val query = remember { mutableStateOf("") }

    BiblomnemonTheme {
        CategoryManagerContent(
            uiState = CategoryManagerUiState.Success(
                items = sampleCategories.filter {
                    it.title.orEmpty().contains(query.value, ignoreCase = true)
                },
                selection = selected.value,
                query = query.value
            )
        )
    }
}