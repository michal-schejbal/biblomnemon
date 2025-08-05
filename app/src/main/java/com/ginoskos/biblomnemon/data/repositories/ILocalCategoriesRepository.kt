package com.ginoskos.biblomnemon.data.repositories

import com.ginoskos.biblomnemon.data.entities.Category
import com.ginoskos.biblomnemon.data.repositories.storage.database.categories.BookCategoryRelations

interface ILocalCategoriesRepository {
    suspend fun fetch(limit: Int = Int.MAX_VALUE, offset: Int = 0): Result<List<Category>>
    suspend fun fetchByBookId(bookId: String, limit: Int = Int.MAX_VALUE, offset: Int = 0): Result<List<Category>>
    suspend fun insert(item: Category): Result<Category>
    suspend fun insertRelation(relation: BookCategoryRelations): Result<Unit>
    suspend fun deleteRelation(relation: BookCategoryRelations): Result<Unit>
    suspend fun update(item: Category): Result<Unit>
    suspend fun delete(item: Category): Result<Unit>
}