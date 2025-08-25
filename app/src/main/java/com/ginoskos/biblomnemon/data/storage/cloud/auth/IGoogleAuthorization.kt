package com.ginoskos.biblomnemon.data.storage.cloud.auth

import com.ginoskos.biblomnemon.core.auth.ITokenStorage
import com.google.android.gms.auth.api.identity.AuthorizationResult

interface IGoogleAuthorization {
    suspend fun authorize(result: AuthorizationResult): ITokenStorage.TokenSnapshot
    suspend fun revoke()
}