package com.ginoskos.biblomnemon.ui.screens.scanner

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nbaplayers.app.logger.ILogger
import com.ginoskos.biblomnemon.core.scanner.IBarcodeScanner
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch

sealed class ScannerUiState {
    object PermissionRequest : ScannerUiState()
    object PermissionDenied : ScannerUiState()
    object Scanning : ScannerUiState()
    data class Scanned(val isbn: String) : ScannerUiState()
}

sealed class ScannerUiEvent {
    object NavigateBack : ScannerUiEvent()
    object OpenSettings : ScannerUiEvent()
    object PermissionRequest : ScannerUiEvent()
    data class PermissionResult(val granted: Boolean) : ScannerUiEvent()
    data class PreviewReady(val previewView: PreviewView) : ScannerUiEvent()
    data class ScannedResult(val isbn: String) : ScannerUiEvent()
}

class ScannerViewModel(
    private val scanner: IBarcodeScanner,
    private val logger: ILogger
) : ViewModel() {
    private val _uiState = MutableStateFlow<ScannerUiState>(ScannerUiState.PermissionRequest)
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<ScannerUiEvent>(capacity = Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onEvent(event: ScannerUiEvent) {
        viewModelScope.launch {
            logger.d("Event: %s", event)
            _events.send(event)
        }
    }

    fun onPermissionGranted(granted: Boolean) {
        if (granted) {
            _uiState.value = ScannerUiState.Scanning
        } else {
            _uiState.value = ScannerUiState.PermissionDenied
        }
    }

    fun onPreviewReady(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        if (_uiState.value != ScannerUiState.Scanning) return

        scanner.start(previewView, lifecycleOwner)

        viewModelScope.launch {
            scanner.codes.take(1).collect { code ->
                logger.d("Scanned code: %s", code)
                _uiState.value = ScannerUiState.Scanned(code)
                _events.send(ScannerUiEvent.ScannedResult(code))
            }
        }
    }

    fun stopScanning() {
        scanner.stop()
    }

    override fun onCleared() {
        super.onCleared()
        stopScanning()
        _events.close()
    }
}