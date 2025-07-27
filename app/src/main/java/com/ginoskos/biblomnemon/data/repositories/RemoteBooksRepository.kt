package com.ginoskos.biblomnemon.data.repositories

import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.BookSource
import com.ginoskos.biblomnemon.data.repositories.storage.remote.google.GoogleBooksSource
import com.ginoskos.biblomnemon.data.repositories.storage.remote.openlibrary.OpenLibrarySource
import kotlinx.coroutines.withContext

class RemoteBooksRepository(
    private val sources: List<IBooksRepository>,
    private val dispatcher: IDispatcherProvider
) : IBooksRepository {

    override suspend fun fetch(
        limit: Int,
        offset: Int
    ): Result<List<Book>> {
        return Result.success(emptyList<Book>())
    }

    override suspend fun search(query: String, limit: Int, offset: Int): Result<List<Book>> = withContext(dispatcher.io) {
         for (source in sources) {
             val result = source.search(query)

             // Check if the source returned a non-empty result, if not continue with the next source
             if (result.isSuccess && result.getOrNull()?.isNotEmpty() == true) {
                 return@withContext result
             }
        }
        Result.success(emptyList())
    }

    override suspend fun getById(id: String): Result<Book> = withContext(dispatcher.io) {
        val parts = id.split(":", limit = 2)
        if (parts.size != 2) return@withContext Result.failure(Exception("Invalid id format"))
        val (prefix, rawId) = parts

        when (prefix) {
            BookSource.GOOGLE.name -> {
                val source = sources.filterIsInstance<GoogleBooksSource>().firstOrNull()
                source?.getById(rawId)?.mapCatching { it ?: throw Exception("Not found") }
                    ?: Result.failure(Exception("GoogleBooksSource not available"))
            }
            BookSource.OPEN_LIBRARY.name -> {
                val source = sources.filterIsInstance<OpenLibrarySource>().firstOrNull()
                source?.getById(rawId)?.mapCatching { it ?: throw Exception("Not found") }
                    ?: Result.failure(Exception("OpenLibrarySource not available"))
            }
            else -> Result.failure(Exception("Unknown prefix: $prefix"))
        }
    }

    override suspend fun getByIsbn(isbn: String): Result<Book> = withContext(dispatcher.io) {
        for (source in sources) {
            val result = source.getByIsbn(isbn)
            if (result.isSuccess && result.getOrNull() != null) {
                return@withContext Result.success(result.getOrThrow()!!)
            }
        }
        Result.failure(Exception("Book not found for ISBN: $isbn"))
    }
}