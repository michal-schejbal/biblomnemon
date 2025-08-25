package com.ginoskos.biblomnemon.data.repositories

import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.data.entities.Category
import com.ginoskos.biblomnemon.data.storage.database.categories.BookCategoryRelations
import com.ginoskos.biblomnemon.data.storage.database.categories.CategoryDao
import com.ginoskos.biblomnemon.data.storage.database.categories.toDomain
import com.ginoskos.biblomnemon.data.storage.database.categories.toEntity
import kotlinx.coroutines.withContext

class LocalCategoriesRepository(
    private val source: CategoryDao,
    private val dispatcher: IDispatcherProvider
) : ILocalCategoriesRepository {
    override suspend fun fetch(limit: Int, offset: Int): Result<List<Category>> =
        withContext(dispatcher.io) {
            safeDbCall {
                source.fetch(limit, offset)
                    .map { it.toDomain() }
            }
        }

    override suspend fun fetchByBookId(bookId: String, limit: Int, offset: Int): Result<List<Category>> =
        withContext(dispatcher.io) {
            safeDbCall {
                source.fetchByBookId(bookId, limit, offset)
                    .map { it.toDomain() }
            }
        }

    override suspend fun insert(item: Category): Result<Category> =
        withContext(dispatcher.io) {
            safeDbCall {
                val insertion = item.copy(
                    created = System.currentTimeMillis(),
                    updated = System.currentTimeMillis()
                )
                source.insert(insertion.toEntity()).let { id ->
                    insertion.copy(id = id)
                }
            }
        }

    override suspend fun insertRelation(relation: BookCategoryRelations): Result<Unit> =
        withContext(dispatcher.io) {
            safeDbCall {
                source.insertRelation(relation)
            }
        }

    override suspend fun deleteRelation(relation: BookCategoryRelations): Result<Unit> =
        withContext(dispatcher.io) {
            safeDbCall {
                source.deleteRelation(relation.bookId, relation.categoryId)
            }
        }

    override suspend fun update(item: Category): Result<Unit> =
        withContext(dispatcher.io) {
            safeDbCall {
                source.update(item
                    .copy(updated = System.currentTimeMillis())
                    .toEntity()
                )
            }
        }

    override suspend fun delete(item: Category): Result<Unit> =
        withContext(dispatcher.io) {
            safeDbCall {
                source.delete(item.toEntity())
            }
        }
}