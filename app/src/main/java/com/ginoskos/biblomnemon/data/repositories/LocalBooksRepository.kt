package com.ginoskos.biblomnemon.data.repositories

import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.repositories.storage.database.books.BookDao
import com.ginoskos.biblomnemon.data.repositories.storage.database.books.toDomain
import com.ginoskos.biblomnemon.data.repositories.storage.database.books.toEntity
import kotlinx.coroutines.withContext

class LocalBooksRepository(
    private val source: BookDao,
    private val dispatcher: IDispatcherProvider
) : ILocalBooksRepository {

    override suspend fun fetch(limit: Int, offset: Int): Result<List<Book>> =
        withContext(dispatcher.io) {
            safeDbCall {
                source.fetch()
                    .map { it.toDomain() }
            }
        }

    override suspend fun search(query: String, limit: Int, offset: Int): Result<List<Book>> {
        return Result.success(emptyList<Book>())
    }

    override suspend fun getById(id: String): Result<Book?> {
        return Result.success(null)
    }

    override suspend fun getByIsbn(isbn: String): Result<Book?> {
        return Result.success(null)
    }

    override suspend fun insert(item: Book): Result<Unit> =
        withContext(dispatcher.io) {
            safeDbCall {
                source.insert(item.toEntity())
            }
        }


    override suspend fun delete(item: Book): Result<Unit> =
        withContext(dispatcher.io) {
            safeDbCall {
                source.delete(item.toEntity())
            }
        }
}