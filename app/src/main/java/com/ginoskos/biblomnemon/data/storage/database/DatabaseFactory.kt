package com.ginoskos.biblomnemon.data.storage.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class DatabaseFactory {
    fun <T : RoomDatabase> createRoom(context: Context, klass: Class<T>): T {
        return Room.databaseBuilder(
            context,
            klass,
            "${klass.simpleName.lowercase().replace("database", "-database")}.db"
        ).apply {
//            addMigrations()
        }.build()
    }
}