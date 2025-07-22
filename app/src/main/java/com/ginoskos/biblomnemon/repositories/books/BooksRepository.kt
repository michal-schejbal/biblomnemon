package com.ginoskos.biblomnemon.repositories.books

import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.repositories.books.storage.remote.IBooksRemoteSource
import kotlinx.coroutines.withContext

class BooksRepository(
    private val sources: List<IBooksRemoteSource>,
    private val dispatcher: IDispatcherProvider
) : IBooksRepository {
    override suspend fun search(query: String): Result<List<Book>> {
         return withContext(dispatcher.io) {
             for (source in sources) {
                 val result = source.search(query)
                 if (result.isSuccess && result.getOrNull()?.isNotEmpty() == true) {
                     return@withContext result
                 }
             }
            Result.success(emptyList())
        }
    }
}