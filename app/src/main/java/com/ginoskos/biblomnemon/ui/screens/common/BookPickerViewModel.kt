package com.ginoskos.biblomnemon.ui.screens.common

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.repositories.ILocalBooksRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class BookPickerUiState {
    data class Success(
        val items: List<Book>,
        val selection: Book? = null,
        val query: String = ""
    ) : BookPickerUiState()
    data class Error(val message: String) : BookPickerUiState()
    object Loading : BookPickerUiState()
}

sealed class BookPickerUiEvent {
    data class Success(@StringRes val message: Int) : BookPickerUiEvent()
    data class Failure(@StringRes val message: Int) : BookPickerUiEvent()
    object Dismiss : BookPickerUiEvent()
    data class Select(val book: Book) : BookPickerUiEvent()
    data class Search(val query: String) : BookPickerUiEvent()
}

class BookPickerViewModel(
    private val repository: ILocalBooksRepository,
    private val logger: ILogger
) : ViewModel() {
    private var job: Job? = null

    private val _events = Channel<BookPickerUiEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _uiState = MutableStateFlow<BookPickerUiState>(BookPickerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: BookPickerUiEvent) {
        viewModelScope.launch {
            logger.d("Event: %s", event)
            _events.send(event)
        }
    }

    fun fetch(book: Book?) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = BookPickerUiState.Loading

            val result = repository.fetch()
            result.fold(
                onSuccess = { items ->
                    _uiState.value = BookPickerUiState.Success(items, book)
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Fetch failed")
                    _uiState.value = BookPickerUiState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }

    fun search(query: String) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = BookPickerUiState.Loading

            val result = if (query.isBlank()) {
                repository.fetch()
            } else {
                repository.search(query)
            }

            result.fold(
                onSuccess = { items ->
                    val currentState = _uiState.value
                    if (currentState is BookPickerUiState.Success) {
                        _uiState.value = currentState.copy(
                            items = items,
                            query = query
                        )
                    } else {
                        _uiState.value = BookPickerUiState.Success(items, query = query)
                    }
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Search failed for query: %s", query)
                    _uiState.value = BookPickerUiState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }
}
