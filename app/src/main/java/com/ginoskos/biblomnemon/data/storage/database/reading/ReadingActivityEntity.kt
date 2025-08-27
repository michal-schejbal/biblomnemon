package com.ginoskos.biblomnemon.data.storage.database.reading

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.ReadingActivity
import com.ginoskos.biblomnemon.data.storage.database.books.BookEntity
import com.ginoskos.biblomnemon.data.storage.database.books.toDomain

@Entity(
    tableName = ReadingActivityEntity.NAME,
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("bookId"), Index("started")]
)
data class ReadingActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val bookId: String? = null,
    val title: String? = null,
    val description: String? = null,
    val started: Long,
    val ended: Long? = null,
    val pagesRead: Int? = null,
    val created: Long? = System.currentTimeMillis(),
    val updated: Long? = System.currentTimeMillis()
) {
    companion object { const val NAME = "reading_activity" }
}

data class ReadingActivityWithBook(
    @Embedded
    val readingActivity: ReadingActivityEntity,

    @Relation(
        parentColumn = "bookId",
        entityColumn = "id"
    )
    val book: BookEntity?
)

fun ReadingActivity.toEntity() = ReadingActivityEntity(
    id = id,
    bookId = book?.id,
    title = title,
    description = description,
    started = started,
    ended = ended,
    pagesRead = pagesRead,
    created = created,
    updated = updated
)

fun ReadingActivityEntity.toDomain() = ReadingActivity(
    id = id,
    book = bookId?.let { Book(id = bookId) },
    title = title,
    description = description,
    started = started,
    ended = ended,
    pagesRead = pagesRead,
    created = created,
    updated = updated
)

fun ReadingActivityWithBook.toDomain() = ReadingActivity(
    id = readingActivity.id,
    book = book?.toDomain(),
    title = readingActivity.title,
    description = readingActivity.description,
    started = readingActivity.started,
    ended = readingActivity.ended,
    pagesRead = readingActivity.pagesRead,
    created = readingActivity.created,
    updated = readingActivity.updated
)
