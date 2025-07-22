package com.ginoskos.biblomnemon.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.nbaplayers.ui.components.LoadingComponent
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.repositories.books.Book
import com.ginoskos.biblomnemon.ui.components.CardComponent
import com.ginoskos.biblomnemon.ui.components.MessageComponent
import com.ginoskos.biblomnemon.ui.screens.IScreen
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

object SearchScreen : IScreen {
    @Serializable object Identifier
    override val identifier: Any
        get() = Identifier

    override fun register(builder: NavGraphBuilder, navController: NavController) {
        builder.composable<Identifier> {
            val args = it.toRoute<Identifier>()
            Content(
                navController = navController
            )
        }
    }

    @Composable
    override fun Content(navController: NavController) {
        val model: SearchViewModel = koinViewModel()
        val uiState by model.uiState.collectAsStateWithLifecycle()
        val query by model.query.collectAsStateWithLifecycle()

        SearchScreenContent(
            modifier = Modifier,
            uiState = uiState,
            query = query,
            onQueryChange = model::onQueryChange,
            onQueryClear = model::onQueryClear,
//            onClick = // TODO onClick,
        )
    }
}

@Composable
fun SearchScreenContent(
    modifier: Modifier = Modifier,
    uiState: SearchUiState,
    query: String,
    onQueryChange: (String) -> Unit = {},
    onQueryClear: () -> Unit = {},
    onClick: (Book) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onClear = onQueryClear
        )

        when (uiState) {
            is SearchUiState.Empty -> {
                MessageComponent(
                    message = stringResource(id = R.string.search_empty_start_typing),
                    icon = Icons.Default.Search
                )
            }

            is SearchUiState.Loading -> {
                LoadingComponent()
            }

            is SearchUiState.Error -> {
                MessageComponent(
                    message = stringResource(id = R.string.search_error_message, uiState.message),
                    icon = Icons.Default.Clear
                )
            }

            is SearchUiState.Success -> {
                if (uiState.items.isEmpty()) {
                    MessageComponent(
                        message = stringResource(id = R.string.search_no_results),
                        icon = Icons.Default.Search
                    )
                } else {
                    LazyColumn {
                        uiState.grouped.forEach { (initial, items) ->
                            item {
                                Text(
                                    text = initial.toString(),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(items = items, key = { it.id }) {
                                BookListItem(item = it, onClick = { onClick(it) })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = { Text(stringResource(id = R.string.search_searchbar_label)) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = stringResource(id = R.string.search_clear_content_desc))
                }
            }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
}

@Composable
fun BookListItem(item: Book, onClick: () -> Unit) {
    CardComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(item.title, style = MaterialTheme.typography.bodyLarge)
            Text(item.author, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val items = listOf(
        Book("1", "A Brief History of Time", "Stephen Hawking", "9780306406157"),
        Book("2", "The Elegant Universe", "Brian Greene", "9780393338102"),
        Book("3", "The Fabric of the Cosmos", "Brian Greene", "9780375714832"),
        Book("4", "Black Holes and Time Warps", "Kip Thorne", "9780393312768"),
        Book("5", "Six Easy Pieces", "Richard Feynman", "9780465025275"),
        Book("6", "Surely You're Joking, Mr. Feynman!", "Richard Feynman", "9780393355628"),
        Book("7", "The Grand Design", "Stephen Hawking", "9780553384666"),
        Book("8", "Parallel Worlds", "Michio Kaku", "9781400033720"),
        Book("9", "Hyperspace", "Michio Kaku", "9780385477055"),
        Book("10", "Physics of the Impossible", "Michio Kaku", "9780307278821")
    )

    BiblomnemonTheme(darkTheme = false) {
        SearchScreenContent(
            uiState = SearchUiState.Success(
                items = items,
                grouped = items
                    .groupBy { it.title.first().uppercaseChar() }
                    .toSortedMap()
            ),
            query = "physics"
        )
    }
}