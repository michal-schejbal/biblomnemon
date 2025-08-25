package com.ginoskos.biblomnemon.data.storage.remote.openlibrary

import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.BookSource
import com.ginoskos.biblomnemon.data.repositories.forceHttps
import com.squareup.moshi.Json

data class OpenLibraryResponse(
    val docs: List<OpenLibraryDoc>?
)

data class OpenLibraryDoc(
    val key: String?,
    val title: String?,
    @Json(name = "author_name") val authorName: List<String>?,
    val isbn: List<String>?,
    @Json(name = "language") val language: List<String>?,
    @Json(name = "publish_date") val publishDate: List<String>?,
    @Json(name = "publisher") val publisher: List<String>?,
    @Json(name = "first_publish_year") val firstPublishYear: Int?
)

fun OpenLibraryDoc.toDomain() = Book(
    id = key ?: "",
    source = BookSource.OPEN_LIBRARY,
    title = title ?: "",
    authors = authorName?.map { Author(name = it) },
    isbn = isbn?.firstOrNull(),
    language = language?.firstOrNull(),
    covers = listOf(
        "https://covers.openlibrary.org/b/id/$key-S.jpg?default=false",
        "https://covers.openlibrary.org/b/id/$key-M.jpg?default=false",
        "https://covers.openlibrary.org/b/id/$key-L.jpg?default=false"
    ).map { it.forceHttps() },
    publishYear = firstPublishYear,
    publisher = publisher?.firstOrNull(),
)
