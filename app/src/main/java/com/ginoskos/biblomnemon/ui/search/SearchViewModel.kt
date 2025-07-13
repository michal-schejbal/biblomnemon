package com.ginoskos.biblomnemon.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ginoskos.biblomnemon.repositories.books.Book
import com.ginoskos.biblomnemon.repositories.books.IBooksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SearchUiState {
    data object Loading : SearchUiState()
    data class Success(
        val books: List<Book> = emptyList(),
        val query: String = ""
    ) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}

class SearchViewModel(
    private val repository: IBooksRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        _uiState.value = SearchUiState.Success(query = query)
        search(query)
    }

    fun onClearQuery() {
        _uiState.value = SearchUiState.Success(query = "")
    }

    private fun search(query: String) {
        viewModelScope.launch {
            val result = repository.search(query)
            result.fold(
                onSuccess = { items ->
                    _uiState.value = SearchUiState.Success(books = items, query = query)
                },
                onFailure = { throwable ->
                    _uiState.value = SearchUiState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }
}