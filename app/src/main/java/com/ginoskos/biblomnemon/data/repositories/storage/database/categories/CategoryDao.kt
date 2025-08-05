package com.ginoskos.biblomnemon.data.repositories.storage.database.categories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDao {
    @Query("SELECT * FROM ${CategoryEntity.NAME} LIMIT :limit OFFSET :offset")
    suspend fun fetch(limit: Int, offset: Int): List<CategoryEntity>

    @Query("""
        SELECT c.* FROM ${CategoryEntity.NAME} c
        INNER JOIN ${BookCategoryRelations.NAME} bcr ON c.id = bcr.categoryId
        WHERE bcr.bookId = :bookId
        LIMIT :limit OFFSET :offset
    """)
    suspend fun fetchByBookId(bookId: String, limit: Int, offset: Int): List<CategoryEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Update()
    suspend fun update(category: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRelation(relation: BookCategoryRelations)

    @Query("""
        DELETE FROM ${BookCategoryRelations.NAME}
        WHERE bookId = :bookId AND categoryId = :categoryId
    """)
    suspend fun deleteRelation(bookId: String, categoryId: Long)


    @Delete
    suspend fun delete(category: CategoryEntity)

    @Query("DELETE FROM ${BookCategoryRelations.NAME} WHERE bookId = :bookId")
    suspend fun clearBookCategories(bookId: String)
}