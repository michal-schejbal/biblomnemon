package com.ginoskos.biblomnemon.core.settings

import com.ginoskos.biblomnemon.data.storage.cloud.CloudUser
import kotlinx.coroutines.flow.Flow

/**
 * Interface for managing application settings.
 */
interface ISettings {
    fun getUser(): Flow<CloudUser?>
    suspend fun setUser(account: CloudUser)
    suspend fun clearUser()

    fun getSpreadsheetId(): Flow<String?>
    suspend fun setSpreadsheetId(id: String)
    suspend fun clearSpreadsheetId()
}