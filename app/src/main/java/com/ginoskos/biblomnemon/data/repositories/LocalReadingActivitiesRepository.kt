package com.ginoskos.biblomnemon.data.repositories

import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.data.entities.ReadingActivity
import com.ginoskos.biblomnemon.data.storage.database.reading.ReadingActivityDao
import com.ginoskos.biblomnemon.data.storage.database.reading.toDomain
import com.ginoskos.biblomnemon.data.storage.database.reading.toEntity
import kotlinx.coroutines.withContext

class LocalReadingActivitiesRepository(
    private val source: ReadingActivityDao,
    private val dispatcher: IDispatcherProvider
) : ILocalReadingActivitiesRepository {

    override suspend fun fetch(limit: Int, offset: Int): Result<List<ReadingActivity>> = withContext(dispatcher.io) {
        safeDbCall {
            source.fetchWithBook(limit, offset).map { it.toDomain() }
        }
    }

    override suspend fun getById(id: Long?): Result<ReadingActivity?> = withContext(dispatcher.io) {
        safeDbCall {
            source.getByIdWithBook(id)?.toDomain()
        }
    }

    override suspend fun insert(item: ReadingActivity): Result<ReadingActivity> = withContext(dispatcher.io) {
        safeDbCall {
            val now = System.currentTimeMillis()
            val inserting = item.copy(created = now, updated = now)
            val id = source.insert(inserting.toEntity())
            inserting.copy(id = id)
        }
    }

    override suspend fun update(item: ReadingActivity): Result<Unit> = withContext(dispatcher.io) {
        safeDbCall {
            source.update(item.copy(updated = System.currentTimeMillis()).toEntity())
        }
    }

    override suspend fun delete(item: ReadingActivity): Result<Unit> = withContext(dispatcher.io) {
        safeDbCall {
            source.delete(item.toEntity())
        }
    }
}
