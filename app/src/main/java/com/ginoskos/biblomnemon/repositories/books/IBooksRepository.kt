package com.ginoskos.biblomnemon.repositories.books

interface IBooksRepository {
    suspend fun search(query: String): Result<List<Book>>
}