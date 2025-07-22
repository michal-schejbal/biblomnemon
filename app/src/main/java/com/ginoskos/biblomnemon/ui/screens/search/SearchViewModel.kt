package com.ginoskos.biblomnemon.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.repositories.books.Book
import com.ginoskos.biblomnemon.repositories.books.IBooksRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

sealed class SearchUiState {
    data object Empty : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(
        val items: List<Book> = emptyList(),
        val grouped: Map<Char, List<Book>> = emptyMap()
    ) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: IBooksRepository
) : ViewModel() {
    private val logger: ILogger by inject(ILogger::class.java)

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    init {
        logger.d("SearchViewModel initialized.")

        _query.debounce(500)
            .onEach { input ->
                logger.d("Query changed (debounced): %s", input)
                if (input.isBlank()) {
                    logger.d("Query is blank -> Empty state")
                    _uiState.value = SearchUiState.Empty
                } else {
                    logger.d("Triggering search for query: %s", input)
                    search(input)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun search(query: String) {
        viewModelScope.launch {
            logger.d("Performing search for: %s", query)
            _uiState.value = SearchUiState.Loading
            val result = repository.search(query)
            result.fold(
                onSuccess = { items ->
                    logger.d("Search success. Found %d items.", items.size)
                    val grouped = items
                        .groupBy { it.title.first().uppercaseChar() }
                        .toSortedMap()
                    _uiState.value = SearchUiState.Success(items = items, grouped = grouped)
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Search failed for query: %s", query)
                    _uiState.value = SearchUiState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }

    fun onQueryChange(query: String) {
        logger.d("onQueryChange: %s", query)
        _query.value = query
    }

    fun onQueryClear() {
        logger.d("onQueryClear")
        _query.value = ""
        _uiState.value = SearchUiState.Empty
    }
}