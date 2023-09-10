package com.example.iot.ui.views

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.iot.ui.views.viewModel.CameraViewModel
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun CameraVideoView(vm: CameraViewModel) {

    //state
    val state by vm.mainState.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    //Image analysis builder
    fun imageAnalysisBuilder(): ImageAnalysis {
        return ImageAnalysis.Builder()
            .setBackpressureStrategy(STRATEGY_KEEP_ONLY_LATEST) //Non blocking strategy. If a frame arrive before analyze end, this frame will be discarded.
            .build()
    }

            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx)
                    val executor = ContextCompat.getMainExecutor(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        //Chose the camera to use for input
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build()

                        //Analyze image
                        val imageAnalyzer = imageAnalysisBuilder()
                        imageAnalyzer.setAnalyzer(
                            executor,
                            ImageAnalysis.Analyzer { imageProxy ->

                                if (!state.loading) {

                                    //Thread.sleep(200)  // wait for 1 second

                                    val imageBitmap = imageProxy.toBitmap()

                                    //Image came from device is rotate by -90°.
                                    //Restore image in 0°
                                    val matrix = Matrix()
                                    matrix.postRotate(90F)
                                    var rotateBitmap: Bitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.width, imageBitmap.height, matrix, true)
                                    rotateBitmap = Bitmap.createScaledBitmap(rotateBitmap, 256, 320, false)

                                    //Create new empty file where to store the result
                                    val file: File = vm.createImageFile("origin")

                                    //Save bitmap into file
                                    val outStream = FileOutputStream(file)
                                    rotateBitmap.compress(
                                        Bitmap.CompressFormat.JPEG,
                                        60,
                                        outStream
                                    )
                                    outStream.flush()
                                    outStream.close()

                                    //Send file to the API
                                    vm.uploadFile(file)
                                }
                                imageProxy.close()
                            })

                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            imageAnalyzer,
                            preview
                        )
                    }, executor)

                    previewView
                },
                modifier = Modifier
                    //.height(500.dp)
                    .fillMaxSize()
            )

}

