package com.ginoskos.biblomnemon.data.repositories.storage.database.categories

import androidx.room.Entity
import androidx.room.ForeignKey
import com.ginoskos.biblomnemon.data.repositories.storage.database.books.BookEntity

@Entity(
    tableName = BookCategoryRelations.NAME,
    primaryKeys = ["bookId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = BookEntity::class,
            parentColumns = ["id"],
            childColumns = ["bookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class BookCategoryRelations(
    val bookId: String,
    val categoryId: Long
) {
    companion object {
        const val NAME = "book_category_relations"
    }
}