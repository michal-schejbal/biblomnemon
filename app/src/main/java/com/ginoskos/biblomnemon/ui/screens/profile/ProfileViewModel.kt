package com.ginoskos.biblomnemon.ui.screens.profile

import android.content.Intent
import android.content.IntentSender
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.example.nbaplayers.model.IDispatcherProvider
import com.ginoskos.biblomnemon.data.storage.cloud.CloudUser
import com.ginoskos.biblomnemon.data.storage.cloud.auth.GoogleAuthManager
import com.ginoskos.biblomnemon.data.storage.cloud.auth.ICloudAuthManager
import com.ginoskos.biblomnemon.data.storage.cloud.storage.ICloudStorageManager
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class SignedIn(
        val user: CloudUser? = null,
        val isSyncing: Boolean = false
    ) : ProfileUiState()
    data object SignedOut : ProfileUiState()
    data object AuthorizationRequired : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

sealed class ProfileUiEvent {
    object NavigateBack : ProfileUiEvent()
    object SignIn : ProfileUiEvent()
    object SignOut : ProfileUiEvent()
    data class AuthorizationRequest(val intent: IntentSender) : ProfileUiEvent()
    data class AuthorizationResponse(val intent: Intent) : ProfileUiEvent()
    object Sync : ProfileUiEvent()
}

class ProfileViewModel(
    private val auth: ICloudAuthManager,
    private val cloud: ICloudStorageManager,
    private val logger: ILogger,
    private val dispatcher: IDispatcherProvider
) : ViewModel() {
    private val _events = Channel<ProfileUiEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.SignedOut)
    val uiState = _uiState.asStateFlow()


    fun onEvent(event: ProfileUiEvent) = viewModelScope.launch {
        logger.d("Event: %s", event)
        _events.send(event)
    }

    fun fetchUser() = viewModelScope.launch {
        if (auth.isSignedIn()) {
            val authorized = auth.isAuthorized()
            if (!authorized) {
                _uiState.value = ProfileUiState.AuthorizationRequired
            } else {
                auth.getUser().collect {
                    _uiState.value = ProfileUiState.SignedIn(it)
                }
            }
        } else {
            _uiState.value = ProfileUiState.SignedOut
        }
    }

    fun signIn() = viewModelScope.launch {
        withContext(dispatcher.io) {
            val result = (auth as? GoogleAuthManager)?.signInRequest()
            result?.fold(
                onFailure = { error ->
                    logger.d("SignIn error: ${error.message}")
                },
                onSuccess = {
                    fetchUser()

                    val authorized = auth.isAuthorized()
                    if (!authorized) {
                        authRequest()
                    }
                }
            )
        }
    }

    fun signOut() = viewModelScope.launch {
        (auth as? GoogleAuthManager)?.signOut()?.fold(
            onSuccess = {
                _uiState.value = ProfileUiState.SignedOut
            },
            onFailure = { error ->
                logger.d("SignOut error: ${error.message}")
            }
        )
    }

    fun authRequest() = viewModelScope.launch {
        val scopes = listOf(
            "https://www.googleapis.com/auth/drive.file", // Also drive.appdata could be use if hidden app folder is needed
            "https://www.googleapis.com/auth/spreadsheets"
        )

        val result = (auth as? GoogleAuthManager)?.authorizationRequest(scopes)

        result?.fold(
            onSuccess = { request ->
                when (request) {
                    is ICloudAuthManager.AuthRequest.None -> {
                        // Done, tokens are updated
                        fetchUser()
                    }

                    is ICloudAuthManager.AuthRequest.Resolution -> {
                        val sender = request.data as IntentSender
                        _events.send(ProfileUiEvent.AuthorizationRequest(sender))
                    }
                }
            },
            onFailure = { error ->
                logger.d("SignIn error: ${error.message}")
            }
        )
    }

    fun authResult(data: Intent) = viewModelScope.launch {
        auth.authorizationResult(ICloudAuthManager.AuthResult.Resolution(data))
    }

    fun sync() = viewModelScope.launch {
        (_uiState.value as? ProfileUiState.SignedIn)?.let { state ->
            _uiState.value = state.copy(isSyncing = true)
        } ?: return@launch

        val result = cloud.upload()
        result.fold(
            onSuccess = {
                logger.i("Cloud upload finished successfully")
                (_uiState.value as? ProfileUiState.SignedIn)?.let { state ->
                    _uiState.value = state.copy(isSyncing = false)
                }
            },
            onFailure = { error ->
                logger.e(error, "Cloud upload failed")
                (_uiState.value as? ProfileUiState.SignedIn)?.let { state ->
                    _uiState.value = state.copy(isSyncing = false)
                }
            }
        )
    }
}