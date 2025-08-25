package com.ginoskos.biblomnemon.data.storage.database.categories

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.ginoskos.biblomnemon.data.storage.database.books.BookEntity

data class BookWithCategories(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = BookCategoryRelations::class,
            parentColumn = "bookId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<CategoryEntity>
)