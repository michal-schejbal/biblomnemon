package com.ginoskos.biblomnemon.ui.screens.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.ui.navigation.NavigationRoute
import com.ginoskos.biblomnemon.ui.screens.MainScreen
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.ThemeLayout
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import com.ginoskos.biblomnemon.ui.theme.components.MessageComponent
import org.koin.androidx.compose.koinViewModel


@Composable
fun LibraryScreen(navController: NavHostController) {
    val model: LibraryViewModel = koinViewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val transfer: BookTransferViewModel = koinViewModel()
    val query by model.query.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        model.fetch()
    }

    MainScreen(
        navController = navController,
        title = stringResource(id = R.string.nav_library),
        fab = {
            FloatingActionButton(
                onClick = { navController.navigate(NavigationRoute.LibraryEdit) },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(ThemeLayout.offset)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Book",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) {
        LibraryScreenContent(
            uiState = uiState,
            query = query,
            onQueryChange = model::onQueryChange,
            onQueryClear = model::onQueryClear,
            onClick = { item ->
                transfer.put(item)
                navController.navigate(NavigationRoute.LibraryEdit)
            },
        )
    }
}

@Composable
fun LibraryScreenContent(
    modifier: Modifier = Modifier,
    uiState: LibraryUiState,
    query: String,
    onQueryChange: (String) -> Unit = {},
    onQueryClear: () -> Unit = {},
    onClick: (Book) -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onClear = onQueryClear
        )

        when (uiState) {
            is LibraryUiState.Empty -> {
                MessageComponent(
                    message = stringResource(id = R.string.search_empty_start_typing),
                    iconVector = Icons.Default.Search
                )
            }

            is LibraryUiState.Loading -> {
                LoadingComponent()
            }

            is LibraryUiState.Error -> {
                MessageComponent(
                    message = stringResource(id = R.string.search_error_message, uiState.message),
                    iconVector = Icons.Default.Clear
                )
            }

            is LibraryUiState.Success -> {
                if (uiState.items.isEmpty()) {
                    MessageComponent(
                        message = stringResource(id = R.string.search_no_results),
                        iconVector = Icons.Default.Search
                    )
                } else {
                    LazyColumn {
                        uiState.items.forEach { (initial, items) ->
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



@Preview(showBackground = true)
@Composable
fun LibraryScreenPreview() {
    val items = listOf(
        Book(id = "1", title = "A Brief History of Time", authors = listOf(Author(name = "Stephen Hawking")), publishYear = 1988),
        Book(id = "2", title = "The Elegant Universe", authors = listOf(Author(name = "Brian Greene")), publishYear = 1999),
        Book(id = "3", title = "The Fabric of the Cosmos", authors = listOf(Author(name = "Brian Greene")), publishYear = 2004),
        Book(id = "4", title = "Black Holes and Time Warps", authors = listOf(Author(name = "Kip Thorne")), publishYear = 1994),
        Book(id = "5", title = "Six Easy Pieces", authors = listOf(Author(name = "Richard Feynman")), publishYear = 1994),
        Book(id = "6", title = "Surely You're Joking, Mr. Feynman!", authors = listOf(Author(name = "Richard Feynman")), publishYear = 1985),
        Book(id = "7", title = "The Grand Design", authors = listOf(Author(name = "Stephen Hawking")), publishYear = 2010),
        Book(id = "8", title = "Parallel Worlds", authors = listOf(Author(name = "Michio Kaku")), publishYear = 2004),
        Book(id = "9", title = "Hyperspace", authors = listOf(Author(name = "Michio Kaku")), publishYear = 1994),
        Book(id = "10", title = "Physics of the Impossible", authors = listOf(Author(name = "Michio Kaku")), publishYear = 2008)
    )

    BiblomnemonTheme(darkTheme = false) {
        LibraryScreenContent(
            uiState = LibraryUiState.Success(
                items = items
                    .groupBy { it.title!!.first().uppercaseChar() }
                    .toSortedMap()
            ),
            query = "physics"
        )
    }
}