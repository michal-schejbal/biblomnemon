package com.ginoskos.biblomnemon.data.repositories

import com.ginoskos.biblomnemon.data.entities.Book

interface ILocalBooksRepository : IBooksRepository {
    suspend fun insert(item: Book): Result<Unit>
    suspend fun delete(item: Book): Result<Unit>
}