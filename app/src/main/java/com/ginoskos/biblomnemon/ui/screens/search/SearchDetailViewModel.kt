package com.ginoskos.biblomnemon.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.repositories.IBooksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SearchDetailUiState {
    data object Empty : SearchDetailUiState()
    data object Loading : SearchDetailUiState()
    data class Success(
        val item: Book? = null
    ) : SearchDetailUiState()
    data class Error(val message: String) : SearchDetailUiState()
}

class SearchDetailViewModel(
    private val remoteRepository: IBooksRepository,
    private val logger: ILogger
) : ViewModel() {
    private val _uiState = MutableStateFlow<SearchDetailUiState>(SearchDetailUiState.Empty)
    val uiState = _uiState.asStateFlow()

    fun fetch(item: Book) {
        viewModelScope.launch {
            _uiState.value = SearchDetailUiState.Loading
            val result = remoteRepository.getById("${item.source.name}:${item.id}")
            result.fold(
                onSuccess = { item ->
                    _uiState.value = SearchDetailUiState.Success(item = item)
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Fetch failed for item: %s", item.id)
                    _uiState.value = SearchDetailUiState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }
}