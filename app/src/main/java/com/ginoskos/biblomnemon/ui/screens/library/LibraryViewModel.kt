package com.ginoskos.biblomnemon.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.repositories.ILocalBooksRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

sealed class LibraryUiState {
    data object Empty : LibraryUiState()
    data object Loading : LibraryUiState()
    data class Success(
        val items: Map<Char, List<Book>> = emptyMap(),
    ) : LibraryUiState()
    data class Error(val message: String) : LibraryUiState()
}

@OptIn(FlowPreview::class)
class LibraryViewModel(
    private val repository: ILocalBooksRepository
) : ViewModel() {
    private val logger: ILogger by inject(ILogger::class.java)
    private var job: Job? = null

    private val _uiState = MutableStateFlow<LibraryUiState>(LibraryUiState.Empty)
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    init {
        _query.debounce(500)
            .onEach { input ->
                if (input.isNotBlank()) {
                    search(input)
                }
            }
            .launchIn(viewModelScope)
    }

    fun fetch() {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading
            val result = repository.fetch()
            result.fold(
                onSuccess = { items ->
                    val grouped = items.groupBy { it.title.first().uppercaseChar() }.toSortedMap()
                    _uiState.value = LibraryUiState.Success(items = grouped)
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Search failed for query: %s", query)
                    _uiState.value = LibraryUiState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }

    fun search(query: String) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = LibraryUiState.Loading
            val result = repository.search(query)
            result.fold(
                onSuccess = { items ->
                    val grouped = items.groupBy { it.title.first().uppercaseChar() }.toSortedMap()
                    _uiState.value = LibraryUiState.Success(items = grouped)
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Search failed for query: %s", query)
                    _uiState.value = LibraryUiState.Error(throwable.message ?: "Unknown error")
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
        _uiState.value = LibraryUiState.Empty
    }
}