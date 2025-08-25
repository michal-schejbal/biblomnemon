package com.ginoskos.biblomnemon.data.storage.cloud.storage

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.memberProperties
import androidx.room.ColumnInfo
import com.ginoskos.biblomnemon.data.entities.Book
import com.ginoskos.biblomnemon.data.entities.Category
import com.ginoskos.biblomnemon.data.storage.database.books.BookEntity
import com.ginoskos.biblomnemon.data.storage.database.categories.BookCategoryRelations
import com.ginoskos.biblomnemon.data.storage.database.categories.CategoryEntity
import kotlin.collections.emptyList
import kotlin.reflect.jvm.javaField

// Books
object BookSheet {
    const val TITLE = BookEntity.NAME
    val headers = headersFromEntity(BookEntity::class)
}

fun Book.toRow(): List<Any> = listOf(
    id,
    source.name,
    title,
    description ?: "",
    authors?.joinToString("; ") { it.name } ?: "",
    isbn ?: "",
    language ?: "",
    covers?.joinToString("; ") ?: "",
    publishYear ?: "",
    publisher ?: "",
    pageCount ?: "",
    created ?: "",
    updated ?: ""
)

// Categories
object CategorySheet {
    const val TITLE = CategoryEntity.NAME
    val headers = headersFromEntity(CategoryEntity::class)
}
fun Category.toRow(): List<Any> = listOf(
    id ?: "",
    title ?: "",
    created ?: "",
    updated ?: ""
)

// Book–Category
object RelationSheet {
    const val TITLE = BookCategoryRelations.NAME
    val headers = headersFromEntity(BookCategoryRelations::class)
}
fun relationRow(bookId: String, categoryId: Long?): List<Any> =
    listOf(bookId, categoryId ?: "")


private fun <T : Any> headersFromEntity(
    klass: KClass<T>,
    exclude: List<String> = emptyList()
): List<String> {
    val propToHeader: Map<String, String> = klass.memberProperties.associate { prop ->
        val colAnn = prop.javaField?.getAnnotation(ColumnInfo::class.java)
        val header = colAnn?.name?.takeIf { !it.isNullOrBlank() } ?: prop.name
        prop.name to header
    }

    val orderedPropNames: List<String> =
        klass.primaryConstructor?.parameters
            ?.mapNotNull { it.name }  // guard against null names
            ?: klass.memberProperties.map { it.name } // fallback (order not guaranteed)

    return orderedPropNames
        .map { propName -> propToHeader[propName] ?: propName }
        .filter { headerOrProp ->
            val propName = orderedPropNames.getOrNull(
                orderedPropNames.indexOfFirst { propToHeader[it] == headerOrProp } // map back to prop if possible
            )
            headerOrProp !in exclude && (propName == null || propName !in exclude)
        }
}