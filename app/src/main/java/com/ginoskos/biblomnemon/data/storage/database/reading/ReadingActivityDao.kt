package com.ginoskos.biblomnemon.data.storage.database.reading

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface ReadingActivityDao {
    @Query("SELECT * FROM ${ReadingActivityEntity.NAME} ORDER BY started DESC LIMIT :limit OFFSET :offset")
    suspend fun fetch(limit: Int = 100, offset: Int = 0): List<ReadingActivityEntity>

    @Transaction
    @Query("SELECT * FROM ${ReadingActivityEntity.NAME} ORDER BY started DESC LIMIT :limit OFFSET :offset")
    suspend fun fetchWithBook(limit: Int = 100, offset: Int = 0): List<ReadingActivityWithBook>

    @Query("SELECT * FROM ${ReadingActivityEntity.NAME} WHERE id = :id")
    suspend fun getById(id: Long?): ReadingActivityEntity?

    @Transaction
    @Query("SELECT * FROM ${ReadingActivityEntity.NAME} WHERE id = :id")
    suspend fun getByIdWithBook(id: Long?): ReadingActivityWithBook?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ReadingActivityEntity): Long

    @Update
    suspend fun update(item: ReadingActivityEntity)

    @Delete
    suspend fun delete(item: ReadingActivityEntity)
}
