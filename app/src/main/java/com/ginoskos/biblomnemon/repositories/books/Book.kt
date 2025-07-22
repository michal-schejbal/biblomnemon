package com.ginoskos.biblomnemon.repositories.books

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val isbn: String // ISBN-10 ([0-9X]{10}) or ISBN-13 ([0-9]{13})
)