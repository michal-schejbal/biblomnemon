package com.ginoskos.biblomnemon.data.storage.cloud.auth

import com.ginoskos.biblomnemon.data.storage.cloud.CloudUser
import kotlinx.coroutines.flow.Flow

interface ICloudAuthManager {
    sealed interface AuthResult {
        data object None : AuthResult
        data class Resolution(val data: Any) : AuthResult
    }
    sealed interface AuthRequest {
        data object None : AuthRequest
        data class Resolution(val data: Any) : AuthRequest
    }

    suspend fun signInRequest(): Result<Unit>
    suspend fun signInResult(result: AuthResult) // Handling of the signInRequest result
    suspend fun signOut(): Result<Unit>
    suspend fun isSignedIn(): Boolean

    suspend fun authorizationRequest(scopes: List<String>): Result<AuthRequest>
    suspend fun authorizationResult(result: AuthResult): Result<Unit> // Handling of the authRequest result
    suspend fun isAuthorized(): Boolean
    suspend fun authorizationRevoke(): Result<Unit>

    suspend fun getUser(): Flow<CloudUser?>
}