package com.ginoskos.biblomnemon.ui.screens.scanner

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ginoskos.biblomnemon.core.scanner.IBarcodeScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ScanUiState {
    object PermissionRequest : ScanUiState()
    object PermissionDenied : ScanUiState()
    object Scanning : ScanUiState()
    data class Scanned(val isbn: String) : ScanUiState()
}

class ScanViewModel(
    private val scanner: IBarcodeScanner
) : ViewModel() {

    private val _uiState = MutableStateFlow<ScanUiState>(ScanUiState.PermissionRequest)
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

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
            scanner.codes.collect { code ->
                _uiState.value = ScanUiState.Scanned(code)
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