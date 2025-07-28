package com.ginoskos.biblomnemon.core.scanner

import android.annotation.SuppressLint
import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.nbaplayers.app.logger.ILogger
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.java.KoinJavaComponent.inject
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * MLKit + CameraX implementation of [IBarcodeScanner].
 *
 * To use:
 * - Construct with a Context and LifecycleOwner (usually your Activity).
 * - Call startScanning() when ready, stopScanning() when done.
 */
class MLKitBarcodeScanner(
    private val context: Context
) : IBarcodeScanner {
    private val logger: ILogger by inject(ILogger::class.java)

    private val _codes = MutableSharedFlow<String>(extraBufferCapacity = 1)
    override val codes = _codes.asSharedFlow()

    private var cameraExecutor: ExecutorService? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var imageAnalyzer: ImageAnalysis? = null

    private val scanner by lazy {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E
                )
                .build()
        )
    }

    override fun start(previewView: PreviewView, lifecycleOwner: LifecycleOwner) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor!!) { imageProxy ->
                        processImageProxy(imageProxy)
                    }
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    override fun stop() {
        cameraProvider?.unbindAll()
        cameraExecutor?.shutdown()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                barcodes.firstNotNullOfOrNull { it.rawValue }?.let {
                    if (_codes.tryEmit(it)) {
                        logger.d("Code emitted: %s", it)
                    } else {
                        logger.d("Barcode not emitted (buffer full or dropped): %s", it)
                    }
                }
            }
            .addOnFailureListener { e ->
                logger.e(e, "Barcode processing failed.")
            }
            .addOnCompleteListener {
                imageProxy.close()
            }

    }
}