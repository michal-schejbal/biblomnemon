package com.ginoskos.biblomnemon.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.data.entities.ReadingActivity
import com.ginoskos.biblomnemon.data.repositories.ILocalReadingActivitiesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ReadingActivityUiState {
    data object Empty : ReadingActivityUiState()
    data object Loading : ReadingActivityUiState()
    data class Success(
        val items: List<ReadingActivity> = emptyList(),
    ) : ReadingActivityUiState()
    data class Error(val message: String) : ReadingActivityUiState()
}

class ReadingActivityViewModel(
    private val repository: ILocalReadingActivitiesRepository,
    private val logger: ILogger
) : ViewModel() {

    private val _uiState = MutableStateFlow<ReadingActivityUiState>(ReadingActivityUiState.Empty)
    val uiState = _uiState.asStateFlow()

    fun fetch() = viewModelScope.launch {
        _uiState.value = ReadingActivityUiState.Loading
        val result = repository.fetch(limit = 200)
        result.fold(
            onSuccess = { items ->
                _uiState.value = ReadingActivityUiState.Success(items = items)
            },
            onFailure = { throwable ->
                logger.e(throwable, "Failed to fetch reading activities")
                _uiState.value = ReadingActivityUiState.Error(throwable.message ?: "Unknown error")
            }
        )
    }

    fun add(item: ReadingActivity) = viewModelScope.launch {
        repository.insert(item).onSuccess {
            logger.d("Reading activity added successfully")
            fetch()
        }.onFailure { throwable ->
            logger.e(throwable, "Failed to add reading activity")
            _uiState.value = ReadingActivityUiState.Error(throwable.message ?: "Failed to add activity")
        }
    }

    fun delete(item: ReadingActivity) = viewModelScope.launch {
        repository.delete(item).onSuccess {
            logger.d("Reading activity deleted successfully")
            fetch()
        }.onFailure { throwable ->
            logger.e(throwable, "Failed to delete reading activity")
            _uiState.value = ReadingActivityUiState.Error(throwable.message ?: "Failed to delete activity")
        }
    }


}
