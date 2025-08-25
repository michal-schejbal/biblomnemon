package com.ginoskos.biblomnemon.core.auth

import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

/**
 * Abstraction for secure persistence of OAuth tokens.
 *
 * Implements the Memento pattern by exposing a [TokenSnapshot] that captures
 * the complete token state (access token, refresh token, expiration) at a
 * given moment. Implementations are responsible for encrypting, storing,
 * and retrieving this snapshot in a safe and atomic way.
 *
 * Typical usage:
 * - [store] to save the latest token state after sign-in or refresh
 * - [read] to load the current snapshot on demand
 * - [observe] to react to token state changes (e.g. trigger sync or sign-out)
 * - [clear] to remove all locally stored tokens
 */
interface ITokenStorage {
    data class TokenSnapshot(
        val accessToken: String?,
        val refreshToken: String?,
        val expiration: Long? = System.currentTimeMillis() + 60.minutes.toLong(DurationUnit.MILLISECONDS)
    )
    suspend fun store(snapshot: TokenSnapshot)
    suspend fun read(): TokenSnapshot?
    suspend fun clear()
    fun observe(): Flow<TokenSnapshot?>
}