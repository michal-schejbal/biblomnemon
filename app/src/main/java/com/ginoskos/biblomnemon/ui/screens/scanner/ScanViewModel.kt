package com.ginoskos.biblomnemon.ui.screens.scanner

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.core.scanner.IBarcodeScanner
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

sealed class ScanUiState {
    object PermissionRequest : ScanUiState()
    object PermissionDenied : ScanUiState()
    object Scanning : ScanUiState()
    data class Scanned(val isbn: String) : ScanUiState()
}

sealed class ScanUiEvent {
    object NavigateBack : ScanUiEvent()
    object OpenSettings : ScanUiEvent()
    object PermissionRequest : ScanUiEvent()
    data class PermissionResult(val granted: Boolean) : ScanUiEvent()
    data class PreviewReady(val previewView: PreviewView) : ScanUiEvent()
    data class ScannedResult(val isbn: String) : ScanUiEvent()
}

class ScanViewModel(
    private val scanner: IBarcodeScanner
) : ViewModel() {
    private val logger: ILogger by inject(ILogger::class.java)

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.PermissionRequest)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    private val _events = Channel<ScanUiEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEvent(event: ScanUiEvent) {
        viewModelScope.launch {
            logger.d("Event: %s", event)
            _events.send(event)
        }
    }

    fun onPermissionGranted(granted: Boolean) {
        if (granted) {
            _uiState.value = ScanUiState.Scanning
        } else {
            _uiState.value = ScanUiState.PermissionDenied
        }
    }

    fun onPreviewReady(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        if (_uiState.value != ScanUiState.Scanning) return

        scanner.start(previewView, lifecycleOwner)

        viewModelScope.launch {
            scanner.codes.take(1).collect { code ->
                logger.d("Scanned code: %s", code)
                _uiState.value = ScanUiState.Scanned(code)
                _events.send(ScanUiEvent.ScannedResult(code))
            }
        }
    }

    fun stopScanning() {
        scanner.stop()
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning()
    }
}