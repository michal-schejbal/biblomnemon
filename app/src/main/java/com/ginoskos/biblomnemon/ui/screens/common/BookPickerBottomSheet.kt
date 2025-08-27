package com.ginoskos.biblomnemon.ui.screens.common

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.ui.theme.ThemeLayout
import com.ginoskos.biblomnemon.ui.theme.components.CardComponent
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import com.ginoskos.biblomnemon.ui.theme.components.MessageComponent
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookPickerBottomSheet(
    book: Book? = null,
    onDismiss: (Book?) -> Unit
) {
    val model: BookPickerViewModel = koinViewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(book) {
        model.fetch(book)
    }

    LaunchedEffect(model) {
        model.events.collect { event ->
            when (event) {
                is BookPickerUiEvent.Success -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(event.message))
                    }
                }
                is BookPickerUiEvent.Failure -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(context.getString(event.message))
                    }
                }
                is BookPickerUiEvent.Select -> {
                    onDismiss(event.book)
                }
                BookPickerUiEvent.Dismiss -> {
                    (uiState as? BookPickerUiState.Success)?.let {
                        onDismiss(it.selection)
                    }
                }
                is BookPickerUiEvent.Search -> {
                    model.search(event.query)
                }
            }
        }
    }

    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.currentValue }
            .drop(1)
            .collect { value ->
                if (value == SheetValue.Hidden) {
                    (uiState as? BookPickerUiState.Success)?.let {
                        onDismiss(it.selection)
                    }
                }
            }
    }

    ModalBottomSheet(
        onDismissRequest = {
            (uiState as? BookPickerUiState.Success)?.let {
                onDismiss(it.selection)
            }
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
            BookPickerContent(
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
}

@Composable
fun BookPickerContent(
    uiState: BookPickerUiState,
    onAction: (BookPickerUiEvent) -> Unit = {}
) {
    when (uiState) {
        is BookPickerUiState.Loading -> {
            LoadingComponent()
        }

        is BookPickerUiState.Error -> {
            MessageComponent(
                message = stringResource(id = R.string.search_error_message, uiState.message),
                iconVector = Icons.Default.Clear
            )
        }

        is BookPickerUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ThemeLayout.offset),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search field
                OutlinedTextField(
                    value = uiState.query,
                    onValueChange = { query -> onAction(BookPickerUiEvent.Search(query)) },
                    label = { Text(stringResource(R.string.search_searchbar_label)) },
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
                        MessageComponent(
                            modifier = Modifier,
                            message = if (uiState.query.isBlank()) 
                                stringResource(R.string.reading_activity_empty_title)
                            else stringResource(R.string.search_no_results)
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(items = uiState.items, key = { it.id }) { book ->
                            BookListItem(
                                book = book,
                                isSelected = book.id == uiState.selection?.id,
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
private fun BookListItem(
    book: Book,
    isSelected: Boolean,
    onAction: (BookPickerUiEvent) -> Unit
) {
    CardComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onAction(BookPickerUiEvent.Select(book)) }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            AsyncImage(
                model = book.covers?.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                placeholder = painterResource(R.drawable.ic_book),
                error = painterResource(R.drawable.ic_book)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title.orEmpty(),
                    style = MaterialTheme.typography.titleSmall
                )
                if (book.authors?.isNotEmpty() ?: false) {
                    Text(
                        text = book.authors.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}