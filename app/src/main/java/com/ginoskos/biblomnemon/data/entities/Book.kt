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