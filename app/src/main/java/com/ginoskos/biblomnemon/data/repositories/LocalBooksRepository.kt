package com.ginoskos.biblomnemon.data.repositories

import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.BookSource
import com.ginoskos.biblomnemon.data.repositories.storage.database.books.BookDao
import com.ginoskos.biblomnemon.data.repositories.storage.database.books.toDomain
import com.ginoskos.biblomnemon.data.repositories.storage.database.books.toEntity
import com.ginoskos.biblomnemon.data.repositories.storage.database.categories.CategoryDao
import com.ginoskos.biblomnemon.data.repositories.storage.database.categories.toDomain
import kotlinx.coroutines.withContext
import java.util.UUID

class LocalBooksRepository(
    private val sourceBooks: BookDao,
    private val sourceCategories: CategoryDao,
    private val dispatcher: IDispatcherProvider
) : ILocalBooksRepository {

    override suspend fun fetch(limit: Int, offset: Int): Result<List<Book>> =
        withContext(dispatcher.io) {
            safeDbCall {
                sourceBooks.fetch()
                    .map { it.toDomain() }
            }
        }

    override suspend fun search(query: String, limit: Int, offset: Int): Result<List<Book>> {
        return Result.success(emptyList<Book>())
    }

    override suspend fun getById(id: String): Result<Book?> =
        withContext(dispatcher.io) {
            safeDbCall {
                val resultBook = sourceBooks.getById(id) ?: return@safeDbCall null
                val book = resultBook.toDomain()

                val resultCategories = sourceCategories.fetchByBookId(bookId = id, limit = Int.MAX_VALUE, offset = 0)

                val categories = resultCategories.map { it.toDomain() }
                book.copy(categories = categories)
            }
        }

    override suspend fun getByIsbn(isbn: String): Result<Book?> =
        withContext(dispatcher.io) {
            safeDbCall {
                val resultBook = sourceBooks.getByIsbn(isbn) ?: return@safeDbCall null
                val book = resultBook.toDomain()

                val resultCategories = sourceCategories.fetchByBookId(bookId = book.id, limit = Int.MAX_VALUE, offset = 0)

                val categories = resultCategories.map { it.toDomain() }
                book.copy(categories = categories)
            }
        }

    override suspend fun insert(item: Book): Result<Unit> =
        withContext(dispatcher.io) {
            safeDbCall {
                val insertion = if (item.source == BookSource.MANUAL && item.id.isBlank()) {
                    item.copy(
                        id = UUID.randomUUID().toString(),
                        created = System.currentTimeMillis(),
                        updated = System.currentTimeMillis()
                    )
                } else {
                    item.copy(
                        created = System.currentTimeMillis(),
                        updated = System.currentTimeMillis()
                    )
                }
                sourceBooks.insert(insertion.toEntity())
            }
        }

    override suspend fun update(item: Book): Result<Unit> =
        withContext(dispatcher.io) {
            safeDbCall {
                sourceBooks.update(item
                    .copy(updated = System.currentTimeMillis())
                    .toEntity()
                )
            }
        }


    override suspend fun delete(item: Book): Result<Unit> =
        withContext(dispatcher.io) {
            safeDbCall {
                sourceBooks.delete(item.toEntity())
            }
        }
}