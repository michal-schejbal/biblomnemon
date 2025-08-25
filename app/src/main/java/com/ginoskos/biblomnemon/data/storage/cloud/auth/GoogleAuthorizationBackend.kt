package com.ginoskos.biblomnemon.data.storage.cloud.auth

import com.ginoskos.biblomnemon.core.auth.ITokenStorage
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.GenericUrl
import com.google.api.client.json.gson.GsonFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Backend for Google authorization.
 *
 * TODO retrofit/okhttp
 */
class GoogleAuthorizationBackend(
    private val tokenStorage: ITokenStorage,
    private val webClientId: String? = null,
    private val webClientSecret: String? = null
) : IGoogleAuthorization {

    override suspend fun authorize(result: AuthorizationResult): ITokenStorage.TokenSnapshot {
        val code = result.serverAuthCode
            ?: error("Authorization failed or was cancelled: No serverAuthCode provided")

        require(code.isNotBlank()) {
            "Auth code must not be blank"
        }

        val tokenRequest = GoogleAuthorizationCodeTokenRequest(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            "https://oauth2.googleapis.com/token",
            webClientId,
            webClientSecret,
            code,
            ""
        )

        val tokenResponse = withContext(Dispatchers.IO) {
            tokenRequest.execute()
        }
        val expirationTime = System.currentTimeMillis() + (tokenResponse.expiresInSeconds * 1_000)

        return ITokenStorage.TokenSnapshot(
            accessToken = tokenResponse.accessToken,
            refreshToken = tokenResponse.refreshToken,
            expiration = expirationTime
        ).apply {
            tokenStorage.store(this)
        }
    }

    override suspend fun revoke() {
        val snapshot = tokenStorage.read()
        if (snapshot == null) {
            tokenStorage.clear()
            return
        }

        val token =
            snapshot.refreshToken ?:
            snapshot.accessToken

        requireNotNull(token) {
            "Token must not be null"
        }

        withContext(Dispatchers.IO) {
            val url = "https://oauth2.googleapis.com/revoke?token=$token"
            val request = GoogleNetHttpTransport.newTrustedTransport().createRequestFactory()
                .buildGetRequest(GenericUrl(url))
            val response = request.execute()
            if (!response.isSuccessStatusCode) {
                error("Token revocation failed: ${response.statusCode}")
            }
        }

        tokenStorage.clear()
    }
}