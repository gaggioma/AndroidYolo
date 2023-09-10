package com.example.iot.ui.views.viewModel.states

import android.graphics.Bitmap
import com.example.iot.repositories.model.MlModel
import java.io.File

data class CameraViewModelState(
    val loading: Boolean = false,
    val error: String = "",
    val data: List<MlModel> = listOf<MlModel>(),
    val file: File? = null,
    val fileOrigin: File? = null,
    val externalCacheDir: File? = null,
    val imgBitmap: Bitmap? = null,
    val frameRate: Float = 0.0F,
    val responseDimension: Int = 0
)
