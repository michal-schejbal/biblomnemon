package com.ginoskos.biblomnemon.core.scanner

import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.Flow

/**
 * Abstraction for scanning barcodes (ISBN)
 * Using Flow so UI/ViewModel can observe asynchronously.
 */
interface IBarcodeScanner {
    val codes: Flow<String> // Emits scanned codes
    fun start(previewView: PreviewView, lifecycleOwner: LifecycleOwner)
    fun stop()
}