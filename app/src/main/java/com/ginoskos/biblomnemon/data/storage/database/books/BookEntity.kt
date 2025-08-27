package com.ginoskos.biblomnemon.data.storage.database.books

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.BookSource

@Entity(tableName = BookEntity.NAME)
data class BookEntity(
    @PrimaryKey val id: String,
    val source: BookSource = BookSource.MANUAL,
    val title: String?,
    val description: String?,
    val authors: List<Author>?,
    val isbn: String?,
    val language: String?,
    val covers: List<String>?,
    val publishYear: Int?,
    val publisher: String?,
    val pageCount: Int?,
    val created: Long? = System.currentTimeMillis(),
    val updated: Long? = System.currentTimeMillis()
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
    covers = covers,
    publishYear = publishYear,
    publisher = publisher,
    pageCount = pageCount,
    created = created,
    updated = updated
)

fun BookEntity.toDomain() = Book(
    id = id,
    source = source,
    title = title,
    description = description,
    authors = authors,
    isbn = isbn,
    language = language,
    covers = covers,
    publishYear = publishYear,
    publisher = publisher,
    pageCount = pageCount,
    created = created,
    updated = updated
)