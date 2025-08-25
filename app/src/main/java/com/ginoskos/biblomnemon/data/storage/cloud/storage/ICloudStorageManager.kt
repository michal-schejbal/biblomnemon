package com.ginoskos.biblomnemon.data.storage.cloud.storage

interface ICloudStorageManager {
    suspend fun upload(): Result<Unit>
    suspend fun download(): Result<Unit>
}