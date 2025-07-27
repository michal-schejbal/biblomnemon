package com.ginoskos.biblomnemon.data.repositories

import com.ginoskos.biblomnemon.data.entities.Book

interface IBooksRepository {
    suspend fun fetch(limit: Int = 20, offset: Int = 0): Result<List<Book>>
    suspend fun search(query: String, limit: Int = 20, offset: Int = 0): Result<List<Book>>
    suspend fun getById(id: String): Result<Book?>
    suspend fun getByIsbn(isbn: String): Result<Book?>
}