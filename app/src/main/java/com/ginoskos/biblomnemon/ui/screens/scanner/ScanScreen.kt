package com.ginoskos.biblomnemon.ui.screens.scanner

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ginoskos.biblomnemon.R
import com.ginoskos.biblomnemon.core.app.navigateBack
import com.ginoskos.biblomnemon.core.app.returnResult
import com.ginoskos.biblomnemon.core.scanner.IBarcodeScanner
import com.ginoskos.biblomnemon.ui.components.CardComponent
import com.ginoskos.biblomnemon.ui.components.MessageComponent
import com.ginoskos.biblomnemon.ui.components.ScreenToolbar
import com.ginoskos.biblomnemon.ui.screens.IScreen
import com.ginoskos.biblomnemon.ui.screens.Screen
import com.ginoskos.biblomnemon.ui.theme.BiblomnemonTheme
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

@Screen
object ScanScreen : IScreen {
    @Serializable object Identifier
    const val SCANNED_ISBN = "scanned_isbn"

    override val identifier: Any get() = Identifier
    override val isNavigationBarsVisible: Boolean get() = false

    override fun register(builder: NavGraphBuilder, navController: NavController) {
        builder.composable<Identifier> {
            Content(navController)
        }
    }

    @Composable
    override fun Content(navController: NavController) {
        val model: ScanViewModel = koinViewModel()
        val uiState by model.uiState.collectAsStateWithLifecycle()
        val lifecycleOwner = LocalLifecycleOwner.current
        val context = LocalContext.current

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            model.onEvent(ScanUiEvent.PermissionResult(granted))
        }

        LaunchedEffect(model) {
            model.events.collect { event ->
                when (event) {
                    ScanUiEvent.NavigateBack -> {
                        navController.navigateBack()
                    }
                    ScanUiEvent.PermissionRequest -> {
                        launcher.launch(Manifest.permission.CAMERA)
                    }
                    is ScanUiEvent.PermissionResult -> {
                        model.onPermissionGranted(event.granted)
                    }
                    ScanUiEvent.OpenSettings -> {
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        ).apply {
                            context.startActivity(this)
                        }
                    }
                    is ScanUiEvent.PreviewReady ->  {
                        model.onPreviewReady(event.previewView, lifecycleOwner)
                    }
                    is ScanUiEvent.ScannedResult -> {
                        navController.returnResult(SCANNED_ISBN, event.isbn)
                    }
                }
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                model.stopScanning()
            }
        }

        ScanScreenContent(
            uiState,
            model = model
        )
    }
}

@Composable
fun ScanScreenContent(
    uiState: ScanUiState,
    model: ScanViewModel
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        ScreenToolbar(onBackClick = { model.onEvent(ScanUiEvent.NavigateBack) }) {
            IconButton(onClick = { model.onEvent(ScanUiEvent.OpenSettings) }) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = stringResource(id = R.string.scan_settings_content_desc),
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        when (uiState) {
            ScanUiState.PermissionDenied,
            ScanUiState.PermissionRequest -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        MessageComponent(
                            modifier = Modifier,
                            message = stringResource(id = R.string.scan_permission_message),
                            iconPainter = painterResource(R.drawable.ic_camera)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            modifier = Modifier.alpha(0.6f),
                            onClick = { model.onEvent(ScanUiEvent.PermissionRequest) }
                        ) {
                            Text(stringResource(id = R.string.scan_permission_button))
                        }
                    }
                }
            }

            ScanUiState.Scanning -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_camera),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = stringResource(id = R.string.scan_align_barcode),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        CardComponent(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.3f)
                        ) {
                            AndroidView(
                                modifier = Modifier.fillMaxSize(),
                                factory = { ctx ->
                                    PreviewView(ctx).apply {
                                        implementationMode =
                                            PreviewView.ImplementationMode.COMPATIBLE
                                        scaleType = PreviewView.ScaleType.FILL_CENTER
                                        post {
                                            model.onEvent(ScanUiEvent.PreviewReady(this))
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }

            is ScanUiState.Scanned -> {
                // Handled in LaunchedEffect
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "ScanScreen - Request Permission")
@Composable
fun ScanScreenRequestPermissionPreview() {
    BiblomnemonTheme {
        ScanScreenContent(
            uiState = ScanUiState.PermissionRequest,
            model = ScanViewModel(object : IBarcodeScanner {
                override val codes = emptyFlow<String>()
                override fun start(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {}
                override fun stop() {}
            })
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "ScanScreen - Scanning")
@Composable
fun ScanScreenScanningPreview() {
    BiblomnemonTheme {
        ScanScreenContent(
            uiState = ScanUiState.Scanning,
            model = ScanViewModel(object : IBarcodeScanner {
                override val codes = emptyFlow<String>()
                override fun start(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {}
                override fun stop() {}
            })
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, name = "ScanScreen - Scanned")
@Composable
fun ScanScreenScannedPreview() {
    BiblomnemonTheme {
        ScanScreenContent(
            uiState = ScanUiState.Scanned(isbn = "9780306406157"),
            model = ScanViewModel(object : IBarcodeScanner {
                override val codes = emptyFlow<String>()
                override fun start(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {}
                override fun stop() {}
            })
        )
    }
}