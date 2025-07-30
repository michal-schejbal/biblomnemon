package com.ginoskos.biblomnemon.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import coil.compose.AsyncImage
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.core.app.navigateBack
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.BookCoverCoverType
import com.ginoskos.biblomnemon.ui.screens.IScreen
import com.ginoskos.biblomnemon.ui.screens.Screen
import com.ginoskos.biblomnemon.ui.screens.ScreenScaffoldHoist
import com.ginoskos.biblomnemon.ui.screens.ScreenToolbar
import com.ginoskos.biblomnemon.ui.screens.ScreenWrapper
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.ChipBackground
import com.ginoskos.biblomnemon.ui.theme.components.CardComponent
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import com.ginoskos.biblomnemon.ui.theme.components.MessageComponent
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Screen
object SearchDetailScreen : IScreen {
    @Serializable data class Identifier(val id: String? = null)
    override val identifier: Any get() = Identifier

    override val hoist = ScreenScaffoldHoist(
        topBar = { navController ->
            ScreenToolbar(onBack = {
                navController.navigateBack()
            })
        },
        bottomBar = {}
    )

    override fun register(builder: NavGraphBuilder, navController: NavHostController) {
        builder.composable<Identifier> { backStack ->
            val id = backStack.toRoute<Identifier>().id
            val model: SearchDetailViewModel = koinViewModel()
            val uiState by model.uiState.collectAsStateWithLifecycle()

            if (id != null) {
                LaunchedEffect(id) {
                    model.fetch(id)
                }
            }

            ScreenWrapper {
                SearchDetailScreenContent(
                    uiState = uiState,
                    onAdd = { item ->
                        model.insert(item)
                        navController.navigateBack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchDetailScreenContent(
    uiState: SearchDetailUiState,
    onAdd: (Book) -> Unit = {}
) {
    when (uiState) {
        is SearchDetailUiState.Empty -> {
            MessageComponent(
                message = stringResource(id = R.string.search_detail_empty)
            )
        }

        is SearchDetailUiState.Loading -> {
            LoadingComponent()
        }

        is SearchDetailUiState.Error -> {
            MessageComponent(
                message = stringResource(id = R.string.search_detail_error_message, uiState.message)
            )
        }

        is SearchDetailUiState.Success -> {
            val book = uiState.item
            if (book == null) {
                MessageComponent(
                    message = stringResource(id = R.string.search_detail_not_found)
                )
            } else {
                val scroll = rememberScrollState()
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scroll),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CardComponent(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = book.coverUrls?.getOrNull(BookCoverCoverType.MEDIUM.ordinal),
                            contentDescription = book.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentScale = ContentScale.FillWidth
                        )
                    }

                    Text(book.title, style = MaterialTheme.typography.displayLarge)
                    book.authors?.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            text = it.joinToString(" · ") { a -> a.name },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    book.publishYear?.let {
                        Text(stringResource(id = R.string.search_detail_published, it), style = MaterialTheme.typography.bodySmall)
                    }
                    book.publisher?.let {
                        Text(stringResource(id = R.string.search_detail_publisher, it), style = MaterialTheme.typography.bodySmall)
                    }
                    book.isbn?.let {
                        Text(stringResource(id = R.string.search_detail_isbn, it), style = MaterialTheme.typography.bodySmall)
                    }

                    book.categories?.takeIf { it.isNotEmpty() }?.let { cats ->
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            items(cats) { cat ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(cat) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = ChipBackground,
                                        labelColor = Color.White
                                    ),
                                )
                            }
                        }
                    }

                    if (!book.description.isNullOrBlank()) {
                        Text(book.description!!, style = MaterialTheme.typography.bodyLarge)
                    }

                    Button(
                        onClick = { onAdd(book) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.search_detail_add_content_desc)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(id = R.string.search_detail_add_to_library))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSearchDetailScreen() {
    BiblomnemonTheme {
        SearchDetailScreenContent(
            uiState = SearchDetailUiState.Success(
                item = Book(
                    id = "1",
                    title = "A Brief History of Time",
                    authors = listOf(Author(name = "Stephen Hawking")),
                    description = "An overview of cosmology and black holes.",
                    publishYear = 1988,
                    categories = listOf("Science", "Physics", "Cosmology")
                )
            )
        )
    }
}