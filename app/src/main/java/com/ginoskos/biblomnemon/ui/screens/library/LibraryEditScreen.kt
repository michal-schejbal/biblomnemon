package com.ginoskos.biblomnemon.ui.screens.library

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.core.koinSharedViewModel
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.ui.navigation.NavigationRoute
import com.ginoskos.biblomnemon.ui.navigation.ObserveResult
import com.ginoskos.biblomnemon.ui.navigation.ScreenToolbar
import com.ginoskos.biblomnemon.ui.navigation.navigateBack
import com.ginoskos.biblomnemon.ui.screens.SubScreen
import com.ginoskos.biblomnemon.ui.screens.common.CategoriesSelectorField
import com.ginoskos.biblomnemon.ui.screens.common.ClearableOutlinedTextField
import com.ginoskos.biblomnemon.ui.screens.scanner.SCANNED_ISBN
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun LibraryEditScreen(navController: NavHostController) {
    val model: LibraryEditViewModel = koinViewModel()
    val transfer: BookTransferViewModel = koinSharedViewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    navController.ObserveResult<String>(SCANNED_ISBN) { isbn ->
        model.setIsbn(isbn)
    }

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
                LibraryEditTopBarActions(
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
        LibraryEditScreenContent(
            uiState = uiState,
            onUpdate = model::update,
            onScan = { navController.navigate(NavigationRoute.Scanner) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryEditScreenContent(
    uiState: LibraryEditUiState,
    onUpdate: (Book) -> Unit = {},
    onScan: () -> Unit = {}
) {
    val scroll = rememberScrollState()

    if (uiState.loading) {
        LoadingComponent(modifier = Modifier.fillMaxWidth())
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        with(uiState.item) {
            // Title
            ClearableOutlinedTextField(
                value = title.orEmpty(),
                onValueChange = { onUpdate(copy(title = it)) },
                label = stringResource(R.string.library_edit_title)
            )

            // Authors
            ClearableOutlinedTextField(
                value = authors.orEmpty().joinToString(", ") { it.name },
                onValueChange = {
                    val updated = it.split(',').mapNotNull { name ->
                        name.trim().takeIf(String::isNotEmpty)?.let(::Author)
                    }
                    onUpdate(copy(authors = updated))
                },
                label = stringResource(R.string.library_edit_author)
            )

            // Publish Year
            ClearableOutlinedTextField(
                value = publishYear?.toString() ?: "",
                onValueChange = {
                    val clean = it.filter { ch -> ch.isDigit() }
                    onUpdate(copy(publishYear = clean.toIntOrNull()))
                },
                label = stringResource(R.string.library_edit_publish_year),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // ISBN with barcode scan
            ClearableOutlinedTextField(
                value = isbn.orEmpty(),
                onValueChange = { onUpdate(copy(isbn = it.filter { c -> c.isDigit() || c == 'X' || c == 'x' })) },
                label = stringResource(R.string.library_edit_isbn),
                trailingIcon = {
                    IconButton(onClick = onScan) {
                        Icon(painter = painterResource(R.drawable.ic_barcode), contentDescription = stringResource(R.string.library_edit_scan_isbn))
                    }
                }
            )

            // Description
            ClearableOutlinedTextField(
                value = description.orEmpty(),
                onValueChange = { onUpdate(copy(description = it)) },
                label = stringResource(R.string.library_edit_description),
                singleLine = false
            )

            // Language
            ClearableOutlinedTextField(
                value = language.orEmpty(),
                onValueChange = { onUpdate(copy(language = it)) },
                label = stringResource(R.string.library_edit_language)
            )

            // Publisher
            ClearableOutlinedTextField(
                value = publisher.orEmpty(),
                onValueChange = { onUpdate(copy(publisher = it)) },
                label = stringResource(R.string.library_edit_publisher)
            )

            // Page Count
            ClearableOutlinedTextField(
                value = pageCount?.toString() ?: "",
                onValueChange = {
                    val digitsOnly = it.filter { ch -> ch.isDigit() }
                    onUpdate(copy(pageCount = digitsOnly.toIntOrNull()))
                },
                label = stringResource(R.string.library_edit_page_count),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // Categories
            CategoriesSelectorField(
                categories = categories.orEmpty(),
                book = this,
                onChanged = { selection ->
                    onUpdate(copy(categories = selection))
                }
            )
        }
    }
}

@Composable
fun LibraryEditTopBarActions(
    isSave: Boolean,
    isEdit: Boolean,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TextButton(onClick = onSaveClick, enabled = isSave) {
            Text(stringResource(R.string.action_save).uppercase())
        }

        if (isEdit) {
            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More actions")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.action_delete)) },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            showDeleteDialog = true
                        }
                    )
                }
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
            title = { Text(stringResource(R.string.library_edit_delete_title)) },
            text = { Text(stringResource(R.string.library_edit_delete_message)) },
            confirmButton = {
                TextButton(onClick = onConfirm) { Text(stringResource(R.string.action_delete)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_cancel)) }
            }
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "Library Add Screen - Expanded")
@Composable
fun LibraryAddScreenPreview() {
    BiblomnemonTheme {
        LibraryEditScreenContent(
            uiState = LibraryEditUiState()
        )
    }
}

