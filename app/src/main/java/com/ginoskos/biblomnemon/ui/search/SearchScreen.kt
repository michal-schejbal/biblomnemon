package com.ginoskos.biblomnemon.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nbaplayers.ui.components.ErrorComponent
import com.example.nbaplayers.ui.components.LoadingComponent
import com.ginoskos.biblomnemon.repositories.books.Book
import com.ginoskos.biblomnemon.ui.components.CardComponent
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import org.koin.androidx.compose.koinViewModel

class SearchScreen {
}

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    model: SearchViewModel = koinViewModel(),
    onClick: (Book) -> Unit
) {
    val uiState by model.uiState.collectAsStateWithLifecycle()

    SearchScreenContent(
        modifier = modifier,
        uiState = uiState,
        onClick = onClick,
        onQueryChange = model::onQueryChange,
        onClear = model::onClearQuery
    )
}

@Composable
fun SearchScreenContent(
    modifier: Modifier = Modifier,
    uiState: SearchUiState,
    onClick: (Book) -> Unit = {},
    onQueryChange: (String) -> Unit = {},
    onClear: () -> Unit = {}
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        when (uiState) {
            is SearchUiState.Loading -> {
                LoadingComponent()
            }

            is SearchUiState.Error -> {
                val message = (uiState as SearchUiState.Error).message
                ErrorComponent(message)
            }

            is SearchUiState.Success -> {
                val successState = uiState as SearchUiState.Success

                SearchBar(
                    query = successState.query,
                    onQueryChange = onQueryChange,
                    onClear = onClear
                )

                if (successState.books.isEmpty()) {
                    Text("No results", style = MaterialTheme.typography.bodyLarge)
                } else {
                    LazyColumn {
                        successState.books
                            .groupBy { it.title.first().uppercaseChar() }
                            .toSortedMap()
                            .forEach { (initial, books) ->
                                item {
                                    Text(
                                        text = initial.toString(),
                                        style = MaterialTheme.typography.titleLarge,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                                items(books.size) { index ->
                                    BookListItem(book = books[index], onClick = { onClick(books[index]) })
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
        label = { Text("Search books...") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun BookListItem(book: Book, onClick: () -> Unit) {
    CardComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(book.title, style = MaterialTheme.typography.bodyLarge)
            Text(book.author, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    val books = listOf(
        Book("Atomic Habits", "James Clear", "123456"),
        Book("A Brief History of Time", "Stephen Hawking", "234567"),
        Book("Deep Work", "Cal Newport", "345678"),
        Book("Digital Minimalism", "Cal Newport", "456789"),
        Book("Clean Code", "Robert C. Martin", "567890")
    )

    val uiState = SearchUiState.Success(
        books = books,
        query = "a"
    )

    BiblomnemonTheme(darkTheme = false) {
        SearchScreenContent(uiState = uiState)
    }
}