package com.ginoskos.biblomnemon.data.storage.database.books

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookDao {
    @Query("SELECT * FROM ${BookEntity.NAME}")
    suspend fun fetch(): List<BookEntity>

    @Query("SELECT * FROM ${BookEntity.NAME} WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): BookEntity?

    @Query("SELECT * FROM ${BookEntity.NAME} WHERE id = :isbn LIMIT 1")
    suspend fun getByIsbn(isbn: String): BookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity)

    @Update
    suspend fun update(book: BookEntity)

    @Delete
    suspend fun delete(book: BookEntity)

    @Query("DELETE FROM ${BookEntity.NAME}")
    suspend fun clear()
}