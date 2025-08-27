package com.ginoskos.biblomnemon.ui.screens.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.data.entities.ReadingActivity
import com.ginoskos.biblomnemon.data.repositories.ILocalReadingActivitiesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReadingActivityEditUiState(
    val item: ReadingActivity = ReadingActivity(
        id = null,
        book = null,
        title = "",
        description = "",
        started = System.currentTimeMillis(),
        ended = null,
        pagesRead = null
    ),
    val isStored: Boolean = false,
    val loading: Boolean = false
)

class ReadingActivityEditViewModel(
    private val localRepository: ILocalReadingActivitiesRepository,
    private val logger: ILogger
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReadingActivityEditUiState())
    val uiState = _uiState.asStateFlow()

    fun fetch(item: ReadingActivity) = viewModelScope.launch {
        val result = localRepository.getById(item.id)
        result.fold(
            onSuccess = { local ->
                _uiState.value = _uiState.value.copy(
                    item = local ?: item,
                    isStored = local != null
                )
            },
            onFailure = { throwable ->
               logger.e(throwable, "Failed to fetch the reading activity")
            }
        )
    }

    fun update(item: ReadingActivity) {
        _uiState.value = _uiState.value.copy(item = item)
    }

    fun delete(item: ReadingActivity) = viewModelScope.launch {
        val result = localRepository.delete(item)
        result.fold(
            onSuccess = { local ->
                logger.d("Reading activity deleted successfully")
            },
            onFailure = { throwable ->
                logger.e(throwable, "Failed to delete the reading activity")
            }
        )
    }

    fun store() = viewModelScope.launch {
        val local = localRepository.getById(_uiState.value.item.id).getOrNull()
        if (local == null) {
            localRepository.insert(item = _uiState.value.item)
        } else {
            localRepository.update(item = _uiState.value.item)
        }
    }
}
