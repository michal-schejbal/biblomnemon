package com.ginoskos.biblomnemon.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.repositories.IBooksRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

sealed class SearchUiState {
    data object Empty : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(
        val items: List<Book> = emptyList()
    ) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val repository: IBooksRepository,
    private val logger: ILogger
) : ViewModel() {
    private var job: Job? = null

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    init {
        _query.debounce(500)
            .onEach { input ->
                if (input.isBlank()) {
                    _uiState.value = SearchUiState.Empty
                } else {
                    search(input)
                }
            }
            .launchIn(viewModelScope)
    }

    fun search(query: String) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            val result = repository.search(query)
            result.fold(
                onSuccess = { items ->
                    _uiState.value = SearchUiState.Success(items = items)
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Search failed for query: %s", query)
                    _uiState.value = SearchUiState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }

    fun onQueryClear() {
        _query.value = ""
        _uiState.value = SearchUiState.Empty
    }
}