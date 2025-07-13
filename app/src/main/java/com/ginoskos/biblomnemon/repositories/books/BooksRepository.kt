package com.ginoskos.biblomnemon.repositories.books

import com.example.nbaplayers.model.IDispatcherProvider
import kotlinx.coroutines.withContext

class BooksRepository(
    private val dispatcher: IDispatcherProvider
) : IBooksRepository {
    override suspend fun search(query: String): Result<List<Book>> {
         return withContext(dispatcher.io) {
            Result.success(emptyList<Book>())
        }
    }
}