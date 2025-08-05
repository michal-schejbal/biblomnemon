package com.ginoskos.biblomnemon.data.repositories.storage.database.categories

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ginoskos.biblomnemon.data.entities.Category

@Entity(tableName = CategoryEntity.NAME)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val title: String? = null
) {
    companion object {
        const val NAME = "categories"
    }
}

fun CategoryEntity.toDomain() = Category(
    id = id,
    title = title
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    title = title
)