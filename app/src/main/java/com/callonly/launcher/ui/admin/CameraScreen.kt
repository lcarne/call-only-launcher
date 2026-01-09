package com.callonly.launcher.ui.admin

import android.net.Uri
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    onPhotoCaptured: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val executor = remember { Executors.newSingleThreadExecutor() }
    val mainExecutor = ContextCompat.getMainExecutor(context)

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Log.e("CameraScreen", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Camera Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.Center)
                .border(2.dp, Color.White)
        ) {
            AndroidView(
                factory = { previewView },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopStart),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onCancel) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(id = com.callonly.launcher.R.drawable.ic_arrow_back),
                    contentDescription = "Cancel",
                    tint = Color.White
                )
            }
            Text(
                "Prendre une photo",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )
            Box(Modifier.size(48.dp)) // Spacer
        }

        // Shutter Button
        Button(
            onClick = {
                val photoFile = File(
                    context.cacheDir,
                    "captured_photo_${System.currentTimeMillis()}.jpg"
                )
                val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                imageCapture.takePicture(
                    outputOptions,
                    executor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            val uri = Uri.fromFile(photoFile)
                            // Ensure UI updates happen on the main thread
                            mainExecutor.execute {
                                onPhotoCaptured(uri)
                            }
                        }

                        override fun onError(exception: ImageCaptureException) {
                            Log.e(
                                "CameraScreen",
                                "Photo capture failed: ${exception.message}",
                                exception
                            )
                        }
                    }
                )
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .size(80.dp),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            // Inner circle
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .border(2.dp, Color.Black, CircleShape)
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            executor.shutdown()
        }
    }
}
