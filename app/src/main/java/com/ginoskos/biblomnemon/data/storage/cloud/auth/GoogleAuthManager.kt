package com.ginoskos.biblomnemon.data.storage.cloud.auth

import android.content.Context
import android.content.Intent
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.ginoskos.biblomnemon.core.settings.ISettings
import com.ginoskos.biblomnemon.core.auth.ITokenStorage
import com.ginoskos.biblomnemon.data.repositories.forceHttps
import com.ginoskos.biblomnemon.data.repositories.runCatchingCancellable
import com.ginoskos.biblomnemon.data.storage.cloud.CloudUser
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class GoogleAuthManager(
    private val context: Context,
    private val tokenStorage: ITokenStorage,
    private val settings: ISettings,
    private val authorization: IGoogleAuthorization,
    private val webClientId: String? = null,
    private val webClientSecret: String? = null
) : ICloudAuthManager {

    override suspend fun signInRequest(): Result<Unit> = runCatchingCancellable {
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setAutoSelectEnabled(true)
                .setServerClientId(webClientId!!)
                .setNonce(generateNonce())
                .build())
            .build()

        val response: GetCredentialResponse = CredentialManager.create(context).getCredential(
            context = context,
            request = request
        )

        val credential = response.credential
        require(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            "Unrecognized credential type."
        }

        val user = GoogleIdTokenCredential.createFrom(credential.data).run {
            CloudUser(
                id = id,
                name = displayName,
                avatar = profilePictureUri?.toString()?.forceHttps()
            )
        }

        settings.setUser(user)
    }

    override suspend fun signInResult(result: ICloudAuthManager.AuthResult) {

    }

    override suspend fun signOut(): Result<Unit> = runCatching {
        settings.clearUser()
        tokenStorage.clear()
    }

    override suspend fun isSignedIn(): Boolean {
        return settings.getUser().firstOrNull() != null
    }


    override suspend fun getUser(): Flow<CloudUser?> {
        return settings.getUser()
    }

    /**
     * Start OAuth consent for the given scopes.
     *
     * If user interaction is required, this returns a PendingIntent's IntentSender
     * that the caller (Activity) should launch via StartIntentSenderForResult.
     * If no UI is needed, tokens are obtained immediately and Result contains null.
     */
    override suspend fun authorizationRequest(scopes: List<String>): Result<ICloudAuthManager.AuthRequest> = runCatchingCancellable {
        require(scopes.isNotEmpty()) {
            "Scopes cannot be empty"
        }

        val request = AuthorizationRequest.Builder()
            .setRequestedScopes(scopes.map { Scope(it) })
            .requestOfflineAccess(webClientId!!, false)
            .build()

        val result = Identity.getAuthorizationClient(context)
            .authorize(request)
            .await()

        if (result.hasResolution()) {
            ICloudAuthManager.AuthRequest.Resolution(
                data = result.pendingIntent!!.intentSender
            )
        } else {
            authorization.authorize(result)
            ICloudAuthManager.AuthRequest.None
        }
    }

    /**
     * Complete the OAuth consent flow after the PendingIntent was launched.
     */
    override suspend fun authorizationResult(result: ICloudAuthManager.AuthResult): Result<Unit> = runCatchingCancellable {
        val intent = when (result) {
            is ICloudAuthManager.AuthResult.Resolution -> {
                result.data as? Intent
                    ?: error("Expected an Intent in AuthResult.data")
            }
            else -> null
        }

        requireNotNull(intent) {
            "Authorization failed: Missing intent"
        }

        val intentResult: AuthorizationResult = Identity.getAuthorizationClient(context)
            .getAuthorizationResultFromIntent(intent)

        authorization.authorize(intentResult)
    }

    override suspend fun isAuthorized(): Boolean {
        return tokenStorage.read()
            ?.accessToken
            ?.isNotBlank()
            ?: false
    }

    override suspend fun authorizationRevoke(): Result<Unit> = runCatchingCancellable {
        // The newest API for revocation is not available yet.
//        val request = RevokeAccessRequest.Builder()
//            .build()
//        Identity.getAuthorizationClient(context)
//            .revokeAccess(request)
//            .await()

        // Must be used fallback api https://stackoverflow.com/questions/78866877/how-to-remove-access-when-using-credential-manager
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        GoogleSignIn.getClient(context, gso)
            .revokeAccess()
            .await()

        authorization.revoke()
    }

    private fun generateNonce(length: Int = 32): String {
        val random = UUID.randomUUID().toString()
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(random.toByteArray())
        return hash.toHexString().substring(0, length)
    }
}