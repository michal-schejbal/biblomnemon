package com.ginoskos.biblomnemon.repositories.books.storage.remote

import com.ginoskos.biblomnemon.repositories.books.Book

interface IBooksRemoteSource {
    suspend fun search(query: String): Result<List<Book>>
}