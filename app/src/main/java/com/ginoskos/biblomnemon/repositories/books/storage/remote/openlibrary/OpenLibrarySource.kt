package com.ginoskos.biblomnemon.repositories.books.storage.remote.openlibrary

import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.core.safeApiCall
import com.ginoskos.biblomnemon.repositories.books.Book
import com.ginoskos.biblomnemon.repositories.books.storage.remote.IBooksRemoteSource
import kotlinx.coroutines.withContext

class OpenLibrarySource(
    private val api: OpenLibraryApi,
    private val dispatcher: IDispatcherProvider
) : IBooksRemoteSource {
    override suspend fun search(query: String): Result<List<Book>> = withContext(dispatcher.io) {
        safeApiCall { api.search(query) }.map { response ->
            response.docs
                ?.mapNotNull { it.toBook() }
                ?: emptyList()
        }
    }
}