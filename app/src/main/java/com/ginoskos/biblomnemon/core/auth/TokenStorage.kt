package com.ginoskos.biblomnemon.core.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ginoskos.biblomnemon.core.security.ICrypto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json


class TokenStorage(
    private val context: Context,
    private val crypto: ICrypto
) : ITokenStorage {
    val json = Json { encodeDefaults = true }

    private object Keys {
        val SNAPSHOT = stringPreferencesKey("snapshot")
    }

    @kotlinx.serialization.Serializable
    private data class Payload(
        val accessToken: String? = null,
        val refreshToken: String? = null,
        val expiration: Long? = null
    )

    private val Context.store: DataStore<Preferences> by preferencesDataStore(name = "token_storage")

    override suspend fun store(snapshot: ITokenStorage.TokenSnapshot) {
        val encoded = encode(snapshot) ?: return
        context.store.edit { preferences ->
            preferences[Keys.SNAPSHOT] = encoded
        }
    }

    override suspend fun read(): ITokenStorage.TokenSnapshot? {
        val decoded = context.store.data
            .map { it[Keys.SNAPSHOT] }.first() ?: return null
        return decode(decoded)
    }

    override suspend fun clear() {
        context.store.edit { it.clear() }
    }

    override fun observe(): Flow<ITokenStorage.TokenSnapshot?> {
        return context.store.data
            .map { it[Keys.SNAPSHOT]?.let(::decode) }
            .distinctUntilChanged()
    }

    private fun encode(snapshot: ITokenStorage.TokenSnapshot): String? {
        val payload = Payload(
            accessToken = snapshot.accessToken,
            refreshToken = snapshot.refreshToken,
            expiration = snapshot.expiration
        )

        return runCatching {
            val encoded = json.encodeToString(Payload.serializer(), payload)
            crypto.encrypt(encoded)
        }.getOrElse {
            null
        }
    }

    private fun decode(cipher: String): ITokenStorage.TokenSnapshot? {
        return runCatching {
            val decrypted = crypto.decrypt(cipher)
            val payload = json.decodeFromString(Payload.serializer(), decrypted)
            ITokenStorage.TokenSnapshot(payload.accessToken, payload.refreshToken, payload.expiration)
        }.getOrElse {
            null
        }
    }
}