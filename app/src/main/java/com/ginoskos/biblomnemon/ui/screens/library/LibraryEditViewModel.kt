package com.ginoskos.biblomnemon.ui.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.mergeBlankWith
import com.ginoskos.biblomnemon.data.repositories.IBooksRepository
import com.ginoskos.biblomnemon.data.repositories.ILocalBooksRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LibraryEditUiState(
    val book: Book = Book(id = "", title = ""),
    val loading: Boolean = false
)

class LibraryEditViewModel(
    private val localRepository: ILocalBooksRepository,
    private val remoteRepository: IBooksRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryEditUiState())
    val uiState: StateFlow<LibraryEditUiState> = _uiState

    fun update(update: (Book) -> Book) {
        _uiState.value = _uiState.value.copy(book = update(_uiState.value.book))
    }

    fun setIsbn(isbn: String) {
        update { it.copy(isbn = isbn) }

        if (isbn.length == 10 || isbn.length == 13) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(loading = true)
                val result = remoteRepository.getByIsbn(isbn)
                _uiState.value = _uiState.value.copy(loading = false)
                result.fold(
                    onSuccess = { item ->
                        _uiState.value = _uiState.value.copy(
                            book = _uiState.value.book.mergeBlankWith(item!!)
                        )
                    },
                    onFailure = {
                    }
                )
            }
        }
    }

    fun insert() {
        viewModelScope.launch {
            localRepository.insert(
                item = _uiState.value.book,
            )
        }
    }
}
