package com.ginoskos.biblomnemon.data.storage.remote.openlibrary

import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.repositories.IBooksRepository
import com.ginoskos.biblomnemon.data.repositories.safeApiCall
import kotlinx.coroutines.withContext

class OpenLibrarySource(
    private val api: OpenLibraryApi,
    private val dispatcher: IDispatcherProvider
) : IBooksRepository {
    override suspend fun fetch(
        limit: Int,
        offset: Int
    ): Result<List<Book>> {
        TODO("Not yet implemented")
    }

    override suspend fun search(query: String, limit: Int, offset: Int): Result<List<Book>> = withContext(dispatcher.io) {
        safeApiCall { api.search(query) }.map { response ->
            response.docs
                ?.map { it.toDomain() }
                ?: emptyList()
        }
    }

    override suspend fun getById(id: String): Result<Book?> = withContext(dispatcher.io) {
        safeApiCall { api.getById(id) }.map { it.toDomain() }
    }

    override suspend fun getByIsbn(isbn: String): Result<Book?> = withContext(dispatcher.io) {
        safeApiCall { api.getByIsbn(isbn) }.map { response ->
            response.docs?.firstOrNull()?.toDomain()
        }
    }
}