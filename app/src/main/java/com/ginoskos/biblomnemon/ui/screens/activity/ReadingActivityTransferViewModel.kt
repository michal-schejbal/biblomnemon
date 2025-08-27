package com.ginoskos.biblomnemon.ui.screens.activity

import androidx.lifecycle.ViewModel
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.data.entities.ReadingActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ReadingActivityTransferViewModel(
    private val logger: ILogger
) : ViewModel() {
    private val _readingActivity = MutableStateFlow<ReadingActivity?>(null)
    val readingActivity = _readingActivity.asStateFlow()

    fun put(item: ReadingActivity) {
        logger.d("ReadingActivity set as `%s`", item.title ?: "Untitled")
        _readingActivity.value = item
    }

    fun get(): ReadingActivity? {
        logger.d("ReadingActivity get as `%s`", readingActivity.value?.title ?: "-")
        return readingActivity.value
    }

    fun pop(block: (ReadingActivity) -> Unit) {
        get()?.let {
            block(it)
            clear()
        }
    }

    fun clear() {
        _readingActivity.value = null
    }
}
