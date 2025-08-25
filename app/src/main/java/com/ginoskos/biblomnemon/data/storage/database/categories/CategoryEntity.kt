package com.ginoskos.biblomnemon.data.storage.database.categories

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ginoskos.biblomnemon.data.entities.Category

@Entity(tableName = CategoryEntity.NAME)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    val title: String? = null,
    val created: Long? = System.currentTimeMillis(),
    val updated: Long? = System.currentTimeMillis()
) {
    companion object {
        const val NAME = "categories"
    }
}

fun CategoryEntity.toDomain() = Category(
    id = id,
    title = title,
    created = created,
    updated = updated
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    title = title,
    created = created,
    updated = updated
)