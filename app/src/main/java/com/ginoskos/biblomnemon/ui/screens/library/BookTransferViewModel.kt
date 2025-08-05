package com.ginoskos.biblomnemon.ui.screens.library

import androidx.lifecycle.ViewModel
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.data.entities.Book
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class BookTransferViewModel(
    private val logger: ILogger
) : ViewModel() {
    private val _book = MutableStateFlow<Book?>(null)
    val book = _book.asStateFlow()

    fun put(item: Book) {
        logger.d("Book set as `%s`", item.title)
        _book.value = item
    }

    fun get(): Book? {
        logger.d("Book get as `%s`", book.value?.title ?: "-")
        return book.value
    }

    fun pop(block: (Book) -> Unit) {
        get()?.let {
            block(it)
            clear()
        }
    }

    fun clear() {
        _book.value = null
    }
}