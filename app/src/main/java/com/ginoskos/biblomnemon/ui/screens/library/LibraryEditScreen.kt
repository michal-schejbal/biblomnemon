package com.ginoskos.biblomnemon.ui.screens.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.core.app.navigateBack
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.ui.components.LoadingComponent
import com.ginoskos.biblomnemon.ui.components.ScreenToolbar
import com.ginoskos.biblomnemon.ui.navigation.ObserveResult
import com.ginoskos.biblomnemon.ui.screens.IScreen
import com.ginoskos.biblomnemon.ui.screens.Screen
import com.ginoskos.biblomnemon.ui.screens.scanner.ScanScreen
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Screen
object LibraryEditScreen : IScreen {
    @Serializable
    object Identifier
    override val identifier: Any get() = Identifier
    override val isNavigationBarsVisible: Boolean get() = false

    override fun register(builder: NavGraphBuilder, navController: NavController) {
        builder.composable<Identifier> {
            Content(navController)
        }
    }

    @Composable
    override fun Content(navController: NavController) {
        val model: LibraryEditViewModel = koinViewModel()
        val uiState by model.uiState.collectAsStateWithLifecycle()

        navController.ObserveResult<String>(ScanScreen.SCANNED_ISBN) { isbn ->
            model.setIsbn(isbn)
        }

        LibraryAddScreenContent(
            uiState = uiState,
            onSave = {
                model.insert()
                navController.navigateBack()
            },
            onBack = {
                navController.navigateBack()
             },
            onScan = { navController.navigate(ScanScreen.Identifier) }
        )
    }
}

@Composable
fun LibraryAddScreenContent(
    uiState: LibraryEditUiState,
    onSave: () -> Unit = {},
    onBack: () -> Unit = {},
    onScan: () -> Unit = {}
) {
    var showMore by remember { mutableStateOf(false) }
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (uiState.loading) {
            LoadingComponent(modifier = Modifier.fillMaxWidth())
        }

        ScreenToolbar(
            onBackClick = onBack,
            content = {
                TextButton(onClick = onSave, enabled = uiState.book.title.isNotBlank()) {
                    Text("Save".uppercase())
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {


            OutlinedTextField(
                value = uiState.book.title,
                onValueChange = { uiState.book.copy(title = it) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.book.authors?.joinToString(", ") { it.name } ?: "",
                onValueChange = {
                    uiState.book.copy(
                        authors = it.split(',').mapNotNull { name ->
                            name.trim().takeIf(String::isNotEmpty)?.let(::Author)
                        })
                },
                label = { Text("Author") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.book.publishYear?.toString() ?: "",
                onValueChange = { uiState.book.copy(publishYear = it.toIntOrNull()) },
                label = { Text("Publish Year") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.book.isbn.orEmpty(),
                onValueChange = { uiState.book.copy(isbn = it) },
                label = { Text("ISBN") },
                trailingIcon = {
                    IconButton(onClick = onScan) {
                        Icon(
                            painter = painterResource(R.drawable.ic_barcode),
                            contentDescription = "Scan ISBN"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            TextButton(
                onClick = { showMore = !showMore },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(if (showMore) "Hide More" else "Show More")
            }

            if (showMore) {
                OutlinedTextField(
                    value = uiState.book.description.orEmpty(),
                    onValueChange = { uiState.book.copy(description = it) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.book.language.orEmpty(),
                    onValueChange = { uiState.book.copy(language = it) },
                    label = { Text("Language") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.book.publisher.orEmpty(),
                    onValueChange = { uiState.book.copy(publisher = it) },
                    label = { Text("Publisher") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.book.pageCount?.toString() ?: "",
                    onValueChange = { uiState.book.copy(pageCount = it.toIntOrNull()) },
                    label = { Text("Page Count") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.book.mainCategory.orEmpty(),
                    onValueChange = { uiState.book.copy(mainCategory = it) },
                    label = { Text("Main Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.book.categories?.joinToString(", ") ?: "",
                    onValueChange = {
                        uiState.book.copy(
                            categories = it.split(',')
                                .mapNotNull { s -> s.trim().takeIf(String::isNotEmpty) })
                    },
                    label = { Text("Categories (comma-separated)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@Preview(showBackground = true, name = "Library Add Screen - Expanded")
@Composable
fun LibraryAddScreenPreview() {
    BiblomnemonTheme {
        LibraryAddScreenContent(LibraryEditUiState())
    }
}

