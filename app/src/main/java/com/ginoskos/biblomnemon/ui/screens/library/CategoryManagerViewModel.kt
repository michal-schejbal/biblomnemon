package com.ginoskos.biblomnemon.ui.screens.library

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.data.entities.Category
import com.ginoskos.biblomnemon.data.repositories.ILocalCategoriesRepository
import com.ginoskos.biblomnemon.data.repositories.storage.database.categories.BookCategoryRelations
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

sealed class CategoryManagerUiState {
    data class Success(
        val items: List<Category>,
        val selection: List<Category> = emptyList(),
        val query: String = ""
    ) : CategoryManagerUiState()
    data class Error(val message: String) : CategoryManagerUiState()
    object Loading : CategoryManagerUiState()
}

sealed class CategoryDialogState {
    object None : CategoryDialogState()
    object Create : CategoryDialogState()
    data class Rename(val item: Category) : CategoryDialogState()
    data class Delete(val item: Category) : CategoryDialogState()
}

sealed class CategoryManagerUiEvent {
    data class Success(@StringRes val message: Int) : CategoryManagerUiEvent()
    data class Failure(@StringRes val message: Int) : CategoryManagerUiEvent()
    object Dismiss : CategoryManagerUiEvent()
    object CreateDialog : CategoryManagerUiEvent()
    data class Create(val title: String) : CategoryManagerUiEvent()
    data class RenameDialog(val item: Category) : CategoryManagerUiEvent()
    data class Rename(val item: Category, val title: String) : CategoryManagerUiEvent()
    data class DeleteDialog(val item: Category) : CategoryManagerUiEvent()
    data class Delete(val item: Category) : CategoryManagerUiEvent()
    data class Select(val item: Category) : CategoryManagerUiEvent()
}

class CategoryManagerViewModel(
    private val repository: ILocalCategoriesRepository,
    private val logger: ILogger
) : ViewModel() {
    private var job: Job? = null

    private val _events = Channel<CategoryManagerUiEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _uiState = MutableStateFlow<CategoryManagerUiState>(CategoryManagerUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _dialog = MutableStateFlow<CategoryDialogState>(CategoryDialogState.None)
    val dialog = _dialog.asStateFlow()

    fun onEvent(event: CategoryManagerUiEvent) {
        viewModelScope.launch {
            logger.d("Event: %s", event)
            _events.send(event)
        }
    }

    fun setDialogState(state: CategoryDialogState) {
        _dialog.value = state
    }

    fun fetch(bookId: String?) {
        job?.cancel()
        job = viewModelScope.launch {
            _uiState.value = CategoryManagerUiState.Loading

            val result = repository.fetch()
            val selection = bookId
                ?.let {
                    repository.fetchByBookId(bookId = it)
                }?.getOrElse { emptyList() }
                ?: emptyList()

            result.fold(
                onSuccess = { items ->
                    _uiState.value = CategoryManagerUiState.Success(items, selection)
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Fetch failed")
                    _uiState.value = CategoryManagerUiState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }

    fun create(title: String) {
        if (title.isBlank()) {
            return
        }

        viewModelScope.launch {
            val category = Category(title = title.trim())
            val result = repository.insert(category)
            result.fold(
                onSuccess =  { inserted ->
                    val current = _uiState.value
                    if (current is CategoryManagerUiState.Success) {
                        _uiState.value = current.copy(
                            items = current.items + inserted
                        )
                    }
                    _events.send(CategoryManagerUiEvent.Success(R.string.library_edit_categories_create_success))
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Failed to create category: %s", title)
                    _events.send(CategoryManagerUiEvent.Failure(R.string.library_edit_categories_create_failure))
                }
            )

            _dialog.value = CategoryDialogState.None
        }
    }

    fun rename(item: Category, title: String) {
        if (title.isBlank()) {
            return
        }

        viewModelScope.launch {
            val updated = item.copy(title = title.trim())
            val result = repository.update(updated)
            result.fold(
                onSuccess = {
                    (_uiState.value as? CategoryManagerUiState.Success)?.let { current ->
                        _uiState.value = current.copy(
                            items = current.items.map { if (it.id == updated.id) updated else it },
                        )
                    }
                    _events.send(CategoryManagerUiEvent.Success(R.string.library_edit_categories_rename_success))
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Failed to rename category: %s", title)
                    _events.send(CategoryManagerUiEvent.Failure(R.string.library_edit_categories_rename_failure))
                }
            )

            _dialog.value = CategoryDialogState.None
        }
    }

    fun delete(item: Category) {
        viewModelScope.launch {
            val result = repository.delete(item)
            result.fold(
                onSuccess = {
                    (_uiState.value as? CategoryManagerUiState.Success)?.let { current ->
                        _uiState.value = current.copy(
                            items = current.items.filterNot { it.id == item.id },
                            selection = current.selection.filterNot { it.id == item.id }
                        )
                    }
                    _events.send(CategoryManagerUiEvent.Success(R.string.library_edit_categories_delete_success))
                },
                onFailure = { throwable ->
                    logger.e(throwable, "Failed to delete category: %s", item.title)
                    _events.send(CategoryManagerUiEvent.Failure(R.string.library_edit_categories_delete_failure))
                }
            )

            _dialog.value = CategoryDialogState.None
        }
    }


    fun select(category: Category, bookId: String?) {
        if (bookId == null) {
            return
        }

        val currentState = _uiState.value

        if (currentState is CategoryManagerUiState.Success) {
            val isSelected = currentState.selection.any { it.id == category.id }
            viewModelScope.launch {
                val result = if (isSelected) {
                    repository.deleteRelation(
                        BookCategoryRelations(
                            bookId = bookId,
                            categoryId = category.id!!
                        )
                    )
                } else {
                    repository.insertRelation(BookCategoryRelations(
                        bookId = bookId,
                        categoryId = category.id!!
                    ))
                }

                result.fold(
                    onSuccess = {
                        val updatedSelection = if (isSelected) {
                            currentState.selection.filterNot { it.id == category.id }
                        } else {
                            currentState.selection + category
                        }
                        _uiState.value = currentState.copy(selection = updatedSelection)
                        _events.send(CategoryManagerUiEvent.Success(R.string.library_edit_categories_selection_success))
                    },
                    onFailure = { throwable ->
                        logger.e(throwable, "Failed to toggle relation for category: %s", category.title)
                        _events.send(CategoryManagerUiEvent.Failure(R.string.library_edit_categories_selection_failure))
                    }
                )
            }
        }
    }


//    fun onQueryChange(query: String) {
//        val filtered = if (query.isBlank()) {
//            _allCategories.value
//        } else {
//            _allCategories.value.filter { it.title.contains(query, ignoreCase = true) }
//        }
//
//        _uiState.value = CategoryManagerUiState.Success(
//            items = filtered,
//            selection = _selected.value,
//            query = query
//        )
//    }
//
//    fun persistRelations() {
//        val bookId = bookId ?: return
//        viewModelScope.launch {
//            _selected.value.forEach { categoryId ->
//                repository.insertRelation(BookCategoryRelations(bookId, categoryId))
//            }
//        }
//    }
//
//    fun getSelectedCategories(): List<Category> =
//        allCategories.value.filter { it.id in selected.value }

    override fun onCleared() {
        super.onCleared()
        _events.close()
    }
}