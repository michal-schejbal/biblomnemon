package com.ginoskos.biblomnemon.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.mergeBlankWith
import com.ginoskos.biblomnemon.data.repositories.IBooksRepository
import com.ginoskos.biblomnemon.data.repositories.ILocalBooksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LibraryEditUiState(
    val item: Book = Book(id = "", title = ""),
    val isStored: Boolean = false,
    val loading: Boolean = false
)

class LibraryEditViewModel(
    private val localRepository: ILocalBooksRepository,
    private val remoteRepository: IBooksRepository,
    private val logger: ILogger
) : ViewModel() {
    private val _uiState = MutableStateFlow(LibraryEditUiState())
    val uiState = _uiState.asStateFlow()

    fun fetch(item: Book) = viewModelScope.launch {
        val result = localRepository.getById(item.id)
        result.fold(
            onSuccess = { local ->
                _uiState.value = _uiState.value.copy(
                    item = local ?: item,
                    isStored = local != null
                )
            },
            onFailure = { throwable ->
               logger.e(throwable, "Failed to fetch the book")
            }
        )
    }

    fun update(item: Book) {
        _uiState.value = _uiState.value.copy(item = item)
    }

    fun delete(item: Book) = viewModelScope.launch {
        val result = localRepository.delete(item)
        result.fold(
            onSuccess = { local ->
                logger.d("Book deleted successfully")
            },
            onFailure = { throwable ->
                logger.e(throwable, "Failed to delete the book")
            }
        )
    }

    fun setIsbn(isbn: String) = viewModelScope.launch {
        update(item = _uiState.value.item.copy(isbn = isbn))

        if (isbn.length == 10 || isbn.length == 13) {
            _uiState.value = _uiState.value.copy(loading = true)
            val result = remoteRepository.getByIsbn(isbn)
            _uiState.value = _uiState.value.copy(loading = false)
            result.fold(
                onSuccess = { item ->
                    _uiState.value = _uiState.value.copy(
                        item = _uiState.value.item.mergeBlankWith(item!!)
                    )
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Failed to fetch the book via ISBN")
                }
            )
        }
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
