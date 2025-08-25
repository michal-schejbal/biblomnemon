package com.ginoskos.biblomnemon.data.storage.cloud.auth

import com.ginoskos.biblomnemon.core.auth.ITokenStorage
import com.google.android.gms.auth.api.identity.AuthorizationResult

class GoogleAuthorizationLocal(
    private val tokenStorage: ITokenStorage
) : IGoogleAuthorization {
    override suspend fun authorize(result: AuthorizationResult): ITokenStorage.TokenSnapshot {
        val token = result.accessToken
            ?: error("Authorization failed or was cancelled: No accessToken provided.")

        require(token.isNotBlank()) {
            "Access token must not be blank"
        }

        return ITokenStorage.TokenSnapshot(
            accessToken = token,
            refreshToken = null
        ).apply {
            tokenStorage.store(this)
        }
    }

    override suspend fun revoke() {
        tokenStorage.clear()
    }
}