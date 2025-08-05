package com.ginoskos.biblomnemon.data.repositories.storage.database.books

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.BookSource
import com.ginoskos.biblomnemon.data.entities.Category

@Entity(tableName = BookEntity.NAME)
data class BookEntity(
    @PrimaryKey val id: String,
    val source: BookSource = BookSource.MANUAL,
    val title: String,
    val description: String?,
    val authors: List<Author>?,
    val isbn: String?,
    val language: String?,
    val coverUrls: List<String>?,
    val publishYear: Int?,
    val publisher: String?,
    val pageCount: Int?,
    val categories: List<Category>?
) {
    companion object {
        const val NAME = "books"
    }
}

fun Book.toEntity() = BookEntity(
    id = id,
    source = source,
    title = title,
    description = description,
    authors = authors,
    isbn = isbn,
    language = language,
    coverUrls = coverUrls,
    publishYear = publishYear,
    publisher = publisher,
    pageCount = pageCount,
    categories = categories
)

fun BookEntity.toDomain() = Book(
    id = id,
    source = source,
    title = title,
    description = description,
    authors = authors,
    isbn = isbn,
    language = language,
    coverUrls = coverUrls,
    publishYear = publishYear,
    publisher = publisher,
    pageCount = pageCount,
    categories = categories
)