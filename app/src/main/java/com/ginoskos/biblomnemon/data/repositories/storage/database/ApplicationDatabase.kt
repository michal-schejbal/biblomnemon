package com.ginoskos.biblomnemon.data.repositories.storage.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ginoskos.biblomnemon.data.repositories.storage.database.books.BookDao
import com.ginoskos.biblomnemon.data.repositories.storage.database.books.BookEntity
import com.ginoskos.biblomnemon.data.repositories.storage.database.categories.BookCategoryRelations
import com.ginoskos.biblomnemon.data.repositories.storage.database.categories.CategoryDao
import com.ginoskos.biblomnemon.data.repositories.storage.database.categories.CategoryEntity

@Database(entities = [
    BookEntity::class,
    CategoryEntity::class, BookCategoryRelations::class
], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ApplicationDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun categoryDao(): CategoryDao
}