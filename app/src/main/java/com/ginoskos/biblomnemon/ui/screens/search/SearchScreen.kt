package com.ginoskos.biblomnemon.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
import com.ginoskos.biblomnemon.ui.screens.MainScreen
import com.ginoskos.biblomnemon.ui.screens.ScreenWrapper
import com.ginoskos.biblomnemon.ui.screens.library.BookListItem
import com.ginoskos.biblomnemon.ui.screens.library.BookTransferViewModel
import com.ginoskos.biblomnemon.ui.screens.scanner.SCANNED_ISBN
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import com.ginoskos.biblomnemon.ui.theme.components.MessageComponent
import org.koin.androidx.compose.koinViewModel


@Composable
fun SearchScreen(navController: NavHostController) {
    MainScreen(
        navController = navController,
        title = stringResource(id = R.string.nav_search),
    ) {
        val model: SearchViewModel = koinViewModel()
        val transfer: BookTransferViewModel = koinSharedViewModel()
        val uiState by model.uiState.collectAsStateWithLifecycle()
        val query by model.query.collectAsStateWithLifecycle()

        navController.ObserveResult<String>(SCANNED_ISBN) { isbn ->
            model.onQueryChange("isbn:$isbn")
        }

        SearchScreenContent(
            uiState = uiState,
            query = query,
            onQueryChange = model::onQueryChange,
            onQueryClear = model::onQueryClear,
            onClick = { item ->
                transfer.put(item)
                navController.navigate(NavigationRoute.SearchDetail)
            },
            onScanClick = {
                navController.navigate(NavigationRoute.Scanner)
            }
        )
    }
}


@Composable
fun SearchScreenContent(
    uiState: SearchUiState,
    query: String,
    onQueryChange: (String) -> Unit = {},
    onQueryClear: () -> Unit = {},
    onClick: (Book) -> Unit = {},
    onScanClick: () -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onClear = onQueryClear,
            onScanClick = onScanClick
        )

        when (uiState) {
            is SearchUiState.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MessageComponent(
                        modifier = Modifier,
                        message = stringResource(id = R.string.search_empty_start_typing),
                        iconVector = Icons.Default.Search
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        modifier = Modifier.alpha(0.6f),
                        onClick = onScanClick
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_barcode),
                            contentDescription = stringResource(id = R.string.search_empty_scan)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(id = R.string.search_empty_scan))
                    }
                }
            }

            is SearchUiState.Loading -> {
                LoadingComponent()
            }

            is SearchUiState.Error -> {
                MessageComponent(
                    message = stringResource(id = R.string.search_error_message, uiState.message),
                    iconVector = Icons.Default.Clear
                )
            }

            is SearchUiState.Success -> {
                if (uiState.items.isEmpty()) {
                    MessageComponent(
                        message = stringResource(id = R.string.search_no_results),
                        iconVector = Icons.Default.Search
                    )
                } else {
                    LazyColumn {
                        items(items = uiState.items, key = { it.id }) {
                            BookListItem(item = it, onClick = { onClick(it) })
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
    onClear: () -> Unit,
    onScanClick: () -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = query,
        onValueChange = onQueryChange,
        label = { Text(stringResource(id = R.string.search_searchbar_label)) },
        trailingIcon = {
            Row {
                IconButton(onClick = onScanClick) {
                    Icon(painterResource(R.drawable.ic_barcode), contentDescription = "Scan ISBN")
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClear) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(id = R.string.search_clear_content_desc)
                        )
                    }
                }
            }
        },
        singleLine = true
    )
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
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
        ScreenWrapper {
            SearchScreenContent(
                uiState = SearchUiState.Success(items = items),
                query = "physics"
            )
        }
    }
}