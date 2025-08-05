package com.ginoskos.biblomnemon.data.repositories.storage.database

import androidx.room.TypeConverter
import com.ginoskos.biblomnemon.data.entities.Author
import com.ginoskos.biblomnemon.data.entities.BookSource
import com.ginoskos.biblomnemon.data.entities.Category
import kotlinx.serialization.json.Json

object Converters {
    private val json = Json { ignoreUnknownKeys = true }

    @TypeConverter
    fun fromBookSource(value: BookSource): String = value.name

    @TypeConverter
    fun toBookSource(value: String): BookSource = BookSource.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>?): String? = value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toStringList(value: String?): List<String>? = value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromAuthors(value: List<Author>?): String? = value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toAuthors(value: String?): List<Author>? = value?.let { json.decodeFromString(it) }

    @TypeConverter
    fun fromCategories(value: List<Category>?): String? = value?.let { json.encodeToString(it) }

    @TypeConverter
    fun toCategories(value: String?): List<Category>? = value?.let { json.decodeFromString(it) }
}