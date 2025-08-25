package com.ginoskos.biblomnemon.core.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ginoskos.biblomnemon.data.storage.cloud.CloudUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class Settings(private val context: Context) : ISettings {
    val json = Json { encodeDefaults = true }

    private object Keys {
        val USER_ACCOUNT = stringPreferencesKey("user_account")
        val SPREADSHEET_ID = stringPreferencesKey("spreadsheet_id")
    }

    private val Context.store: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

    override fun getUser(): Flow<CloudUser?> {
        return context.store.data
            .map { preferences ->
                val jsonString = preferences[Keys.USER_ACCOUNT]
                if (jsonString != null) {
                    try {
                        json.decodeFromString<CloudUser>(jsonString)
                    } catch (e: Exception) {
                        null
                    }
                } else {
                    null
                }
            }
    }

    override suspend fun setUser(account: CloudUser) {
        val jsonString = json.encodeToString(account)
        context.store.edit { preferences ->
            preferences[Keys.USER_ACCOUNT] = jsonString
        }
    }

    override suspend fun clearUser() {
        context.store.edit { preferences ->
            preferences.remove(Keys.USER_ACCOUNT)
        }
    }

    override fun getSpreadsheetId(): Flow<String?> =
        context.store.data.map { it[Keys.SPREADSHEET_ID] }

    override suspend fun setSpreadsheetId(id: String) {
        context.store.edit { it[Keys.SPREADSHEET_ID] = id }
    }

    override suspend fun clearSpreadsheetId() {
        context.store.edit { it.remove(Keys.SPREADSHEET_ID) }
    }
}