package com.ginoskos.biblomnemon.repositories.books.storage.remote.google

import com.ginoskos.biblomnemon.repositories.books.Book

data class GoogleBooksResponse(
    val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    val id: String?,
    val volumeInfo: GoogleVolumeInfo?
)

data class GoogleVolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val industryIdentifiers: List<GoogleIndustryIdentifier>?
)

data class GoogleIndustryIdentifier(
    val type: String?,
    val identifier: String?
)

fun GoogleBookItem.toBook(): Book? {
    val info = volumeInfo ?: return null
    return Book(
        id = this.id ?: "",
        title = info.title ?: return null,
        author = info.authors?.joinToString(", ") ?: "Unknown",
        isbn = info.industryIdentifiers?.firstOrNull { it.type?.startsWith("ISBN") == true }?.identifier ?: ""
    )
}