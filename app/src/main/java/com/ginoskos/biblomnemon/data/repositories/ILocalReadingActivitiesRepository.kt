package com.ginoskos.biblomnemon.data.repositories

import com.ginoskos.biblomnemon.data.entities.ReadingActivity

interface ILocalReadingActivitiesRepository {
    suspend fun fetch(limit: Int = 100, offset: Int = 0): Result<List<ReadingActivity>>
    suspend fun getById(id: Long?): Result<ReadingActivity?>
    suspend fun insert(item: ReadingActivity): Result<ReadingActivity>
    suspend fun update(item: ReadingActivity): Result<Unit>
    suspend fun delete(item: ReadingActivity): Result<Unit>
}
