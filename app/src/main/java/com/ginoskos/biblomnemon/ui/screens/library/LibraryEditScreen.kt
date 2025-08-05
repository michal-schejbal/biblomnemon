package com.ginoskos.biblomnemon.ui.screens.library

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.ginoskos.biblomnemon.data.entities.Category
import com.ginoskos.biblomnemon.ui.navigation.NavigationRoute
import com.ginoskos.biblomnemon.ui.navigation.ObserveResult
import com.ginoskos.biblomnemon.ui.navigation.ScreenToolbar
import com.ginoskos.biblomnemon.ui.navigation.navigateBack
import com.ginoskos.biblomnemon.ui.screens.SubScreen
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
            model.update(book = item)
        }
    }

    SubScreen(
        navController = navController,
        topBar = {
            val uiState by model.uiState.collectAsStateWithLifecycle()
            ScreenToolbar(onBack = {
                navController.navigateBack()
            }) {
                TextButton(onClick = {
                    model.insert()
                    navController.navigateBack()
                }, enabled = uiState.item.title.isNotBlank()) {
                    Text(stringResource(R.string.library_edit_save).uppercase())
                }
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
    var showCategoryManager by remember { mutableStateOf(false) }

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
                value = title,
                onValueChange = { onUpdate(copy(title = it)) },
                label = stringResource(R.string.library_edit_title)
            )

            // Authors
            ClearableOutlinedTextField(
                value = authors?.joinToString(", ") { it.name } ?: "",
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

            ClearableOutlinedTextField(
                value = description.orEmpty(),
                onValueChange = { onUpdate(copy(description = it)) },
                label = stringResource(R.string.library_edit_description),
                singleLine = false
            )

            ClearableOutlinedTextField(
                value = language.orEmpty(),
                onValueChange = { onUpdate(copy(language = it)) },
                label = stringResource(R.string.library_edit_language)
            )

            ClearableOutlinedTextField(
                value = publisher.orEmpty(),
                onValueChange = { onUpdate(copy(publisher = it)) },
                label = stringResource(R.string.library_edit_publisher)
            )

            ClearableOutlinedTextField(
                value = pageCount?.toString() ?: "",
                onValueChange = {
                    val digitsOnly = it.filter { ch -> ch.isDigit() }
                    onUpdate(copy(pageCount = digitsOnly.toIntOrNull()))
                },
                label = stringResource(R.string.library_edit_page_count),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

//            OutlinedTextField(
//                value = categories?.joinToString(", ") { it.title } ?: "None",
//                onValueChange = {},
//                label = { Text("Categories") },
//                readOnly = true,
//                trailingIcon = {
//                    Icon(
//                        imageVector = Icons.Default.Edit,
//                        contentDescription = "Edit Categories"
//                    )
//                },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable { showCategoryManager = true },
//            )
            CategoriesFieldWithChips(
                categories = categories.orEmpty(),
                onEditClick = { showCategoryManager = true }
            )

            if (showCategoryManager) {
                CategoryManagerBottomSheet(
                    bookId = id,
                    onDismiss = { selected ->
                        onUpdate(copy(categories = selected))
                        showCategoryManager = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ClearableOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        trailingIcon = {
            Row {
                if (value.isNotBlank()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
                trailingIcon?.invoke()
            }
        },
        keyboardOptions = keyboardOptions,
        modifier = modifier.fillMaxSize(),
        singleLine = singleLine
    )
}

@Composable
fun CategoriesFieldWithChips(
    categories: List<Category>,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth(),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Box(modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = stringResource(R.string.library_edit_categories),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                if (categories.isEmpty()) {
                    Text(
                        text = stringResource(R.string.library_edit_categories_none),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    FlowRow() {
                        categories.forEach { category ->
                            AssistChip(
                                onClick = {},
                                label = { Text(category.title.orEmpty()) }
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
            ) {
                Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.library_edit_categories_edit))
            }
        }
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

