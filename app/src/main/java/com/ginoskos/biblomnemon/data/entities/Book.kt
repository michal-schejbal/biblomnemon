package com.ginoskos.biblomnemon.data.entities

enum class BookCoverCoverType {
    SMALL, MEDIUM, LARGE
}

data class Book(
    val id: String,
    val source: BookSource = BookSource.MANUAL,
    val title: String,
    val description: String? = null,
    val authors: List<Author>? = null,
    val isbn: String? = null,   // ISBN-10 ([0-9X]{10}) or ISBN-13 ([0-9]{13})
    val language: String? = null,
    val coverUrls: List<String>? = null,
    val publishYear: Int? = null,
    val publisher: String? = null,
    val pageCount: Int? = null,
    val categories: List<String>? = null,
    val mainCategory: String? = null
)

fun Book.mergeBlankWith(other: Book): Book = copy(
    title = title.ifBlank { other.title },
    description = description ?: other.description,
    authors = if (authors.isNullOrEmpty()) other.authors else authors,
    isbn = isbn ?: other.isbn,
    language = language ?: other.language,
    coverUrls = if (coverUrls.isNullOrEmpty()) other.coverUrls else coverUrls,
    publishYear = publishYear ?: other.publishYear,
    publisher = publisher ?: other.publisher,
    pageCount = pageCount ?: other.pageCount,
    categories = if (categories.isNullOrEmpty()) other.categories else categories,
    mainCategory = mainCategory ?: other.mainCategory
)