package com.ginoskos.biblomnemon.repositories.books.storage.remote.openlibrary

import com.ginoskos.biblomnemon.repositories.books.Book
import com.squareup.moshi.Json

data class OpenLibraryResponse(
    val docs: List<OpenLibraryDoc>?
)

data class OpenLibraryDoc(
    val key: String?,
    val title: String?,
    @Json(name = "author_name")
    val authorName: List<String>?,
    val isbn: List<String>?
)

fun OpenLibraryDoc.toBook(): Book? {
    return Book(
        id = key ?: "",
        title = title ?: return null,
        author = authorName?.joinToString(", ") ?: "Unknown",
        isbn = isbn?.firstOrNull() ?: ""
    )
}