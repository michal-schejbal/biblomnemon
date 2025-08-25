package com.ginoskos.biblomnemon.ui.screens.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.CloudSync
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.ui.navigation.ScreenToolbar
import com.ginoskos.biblomnemon.ui.navigation.navigateBack
import com.ginoskos.biblomnemon.ui.screens.SubScreen
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import com.ginoskos.biblomnemon.ui.theme.components.LoadingComponent
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(navController: NavHostController) {
    val model: ProfileViewModel = koinViewModel()
    val uiState by model.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        result.data?.let {
            model.onEvent(ProfileUiEvent.AuthorizationResponse(it))
        }
    }

    LaunchedEffect(model.uiState) {
        model.fetchUser()
    }

    LaunchedEffect(model) {
        model.events.collect { event ->
            when (event) {
                ProfileUiEvent.NavigateBack -> {
                    navController.navigateBack()
                }

                ProfileUiEvent.SignIn -> {
                    model.signIn()
                }
                ProfileUiEvent.SignOut -> {
                    model.signOut()
                }
                is ProfileUiEvent.AuthorizationRequest -> {
                    IntentSenderRequest.Builder(event.intent).build().apply {
                        launcher.launch(this)
                    }
                }
                is ProfileUiEvent.AuthorizationResponse -> {
                    model.authResult(event.intent)
                }

                ProfileUiEvent.Sync -> {
                    model.sync()
                }
                else -> {}
            }
        }
    }

    SubScreen(
        navController = navController,
        topBar = {
            ScreenToolbar(onBack = { model.onEvent(ProfileUiEvent.NavigateBack) })
        }
    ) {
        ProfileScreenContent(
            state = uiState,
            onSignIn = { model.onEvent(ProfileUiEvent.SignIn) },
            onSignOut = { model.onEvent(ProfileUiEvent.SignOut) },
            onAuthorize = { model.authRequest() },
            onSync = { model.onEvent(ProfileUiEvent.Sync) }
        )
    }
}


@Composable
private fun ProfileScreenContent(
    state: ProfileUiState,
    onSignIn: () -> Unit,
    onSignOut: () -> Unit,
    onSync: () -> Unit,
    onAuthorize: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        when (state) {
            ProfileUiState.Loading -> LoadingComponent()
            is ProfileUiState.SignedIn -> ProfileSignedInContent(state, onSync, onSignOut)
            ProfileUiState.SignedOut -> ProfileSignedOutContent(onSignIn)
            ProfileUiState.AuthorizationRequired -> ProfileAuthorizationRequiredContent(onAuthorize)
            else -> {}
        }
    }
}

@Composable
fun ProfileSignedOutContent(
    onSignIn: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            ProfileAvatar()
        }

        Text(
            text = "Not signed in",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Text(
            text = "Sign in to enable cloud sync and keep your reading data backed up.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(onClick = onSignIn) {
            Image(
                painter = painterResource(id = R.drawable.ic_google_g),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text("Sign in with Google")
        }
    }
}

@Composable
private fun ProfileSignedInContent(
    state: ProfileUiState.SignedIn,
    onSyncClick: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ProfileAvatar(state.user?.avatar)

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = state.user?.name ?: "",
                style = MaterialTheme.typography.headlineSmall
            )
            state.user?.email?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CloudSync,
                        contentDescription = "Cloud Sync"
                    )
                    Text(
                        text = "Google Drive Sync",
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Text(
                    text = "Last synced: None",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onSyncClick,
                        enabled = !state.isSyncing
                    ) {
                        if (state.isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Syncing…")
                        } else {
                            Icon(
                                imageVector = Icons.Rounded.Sync,
                                contentDescription = "Sync now"
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Sync now")
                        }
                    }

                    OutlinedButton(onClick = onSignOut) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = "Sign out"
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Sign out")
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileAuthorizationRequiredContent(
    onAuthorize: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Authorization Required", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Please authorize access to Google Drive/Sheets to enable syncing.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAuthorize) {
            Text("Authorize Now")
        }
    }
}

@Composable
private fun ProfileAvatar(
    photo: String? = null
) {
    if (photo.isNullOrBlank()) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
    } else {
        AsyncImage(
            model = photo,
            contentDescription = "Profile picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
    }
}


@Preview(
    showBackground = true,
    name = "Authorization Required"
)
@Composable
fun ProfileAuthorizationRequiredContentPreview() {
    BiblomnemonTheme {
        ProfileAuthorizationRequiredContent(
            onAuthorize = {}
        )
    }
}