package com.ginoskos.biblomnemon.ui.screens.library

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.core.app.navigateBack
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.ui.navigation.ObserveResult
import com.ginoskos.biblomnemon.ui.screens.IScreen
import com.ginoskos.biblomnemon.ui.screens.Screen
import com.ginoskos.biblomnemon.ui.screens.ScreenScaffoldHoist
import com.ginoskos.biblomnemon.ui.screens.ScreenToolbar
import com.ginoskos.biblomnemon.ui.screens.ScreenWrapper
import com.ginoskos.biblomnemon.ui.screens.scanner.ScanScreen
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Screen
object LibraryEditScreen : IScreen {
    @Serializable
    object Identifier
    override val identifier: Any get() = Identifier

    override val hoist = ScreenScaffoldHoist(
        topBar = { navController ->
            val model: LibraryEditViewModel = koinViewModel()
            val uiState by model.uiState.collectAsStateWithLifecycle()

            ScreenToolbar(onBack = {
                navController.navigateBack()
            }) {
                TextButton(onClick = {
                    model.insert()
                    navController.navigateBack()
                }, enabled = uiState.book.title.isNotBlank()) {
                    Text("Save".uppercase())
                }
            }
        },
        bottomBar = {}
    )

    override fun register(builder: NavGraphBuilder, navController: NavHostController) {
        builder.composable<Identifier> {
            val model: LibraryEditViewModel = koinViewModel()
            val uiState by model.uiState.collectAsStateWithLifecycle()

            navController.ObserveResult<String>(ScanScreen.SCANNED_ISBN) { isbn ->
                model.setIsbn(isbn)
            }

            ScreenWrapper {
                LibraryEditScreenContent(
                    uiState = uiState,
                    onUpdate = model::update,
                    onScan = { navController.navigate(ScanScreen.Identifier) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryEditScreenContent(
    uiState: LibraryEditUiState,
    onUpdate: (Book) -> Unit = {},
    onScan: () -> Unit = {}
) {
    var showMore by remember { mutableStateOf(false) }
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
        with(uiState.book) {
            // Title
            ClearableOutlinedTextField(
                value = title,
                onValueChange = { onUpdate(copy(title = it)) },
                label = "Title"
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
                label = "Author"
            )

            // Publish Year
            ClearableOutlinedTextField(
                value = publishYear?.toString() ?: "",
                onValueChange = {
                    val clean = it.filter { ch -> ch.isDigit() }
                    onUpdate(copy(publishYear = clean.toIntOrNull()))
                },
                label = "Publish Year",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            // ISBN with barcode scan
            ClearableOutlinedTextField(
                value = isbn.orEmpty(),
                onValueChange = { onUpdate(copy(isbn = it.filter { c -> c.isDigit() || c == 'X' || c == 'x' })) },
                label = "ISBN",
                trailingIcon = {
                    IconButton(onClick = onScan) {
                        Icon(painter = painterResource(R.drawable.ic_barcode), contentDescription = "Scan ISBN")
                    }
                }
            )

            // Show more toggle
            TextButton(
                onClick = { showMore = !showMore },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (showMore) "Hide More" else "Show More")
            }

            if (showMore) {
                ClearableOutlinedTextField(
                    value = description.orEmpty(),
                    onValueChange = { onUpdate(copy(description = it)) },
                    label = "Description",
                    singleLine = false // allow multiline
                )

                ClearableOutlinedTextField(
                    value = language.orEmpty(),
                    onValueChange = { onUpdate(copy(language = it)) },
                    label = "Language"
                )

                ClearableOutlinedTextField(
                    value = publisher.orEmpty(),
                    onValueChange = { onUpdate(copy(publisher = it)) },
                    label = "Publisher"
                )

                ClearableOutlinedTextField(
                    value = pageCount?.toString() ?: "",
                    onValueChange = {
                        val digitsOnly = it.filter { ch -> ch.isDigit() }
                        onUpdate(copy(pageCount = digitsOnly.toIntOrNull()))
                    },
                    label = "Page Count",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                ClearableOutlinedTextField(
                    value = mainCategory.orEmpty(),
                    onValueChange = { onUpdate(copy(mainCategory = it)) },
                    label = "Main Category"
                )

                ClearableOutlinedTextField(
                    value = categories?.joinToString(", ") ?: "",
                    onValueChange = {
                        val updated = it.split(',')
                            .map { s -> s.trim() }
                            .filter { s -> s.isNotEmpty() }
                        onUpdate(copy(categories = updated.takeIf { it.isNotEmpty() }))
                    },
                    label = "Categories (comma-separated)"
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

