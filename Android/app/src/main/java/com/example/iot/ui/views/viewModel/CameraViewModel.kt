package com.example.iot.ui.views.viewModel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iot.repositories.MlApiRepository
import com.example.iot.ui.views.viewModel.states.CameraViewModelState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import javax.inject.Inject
import kotlin.math.round
import kotlin.system.measureTimeMillis


@HiltViewModel
class CameraViewModel @Inject constructor(
    private val repository : MlApiRepository
): ViewModel() {

    //State
    private val _mainState = MutableStateFlow<CameraViewModelState>(CameraViewModelState())
    val mainState = _mainState

    private fun loading(loading: Boolean){
        _mainState.update {cameraViewModelState ->
            cameraViewModelState.copy(
                loading = loading
            )
        }
    }

    private fun error(error: String){
        _mainState.update {cameraViewModelState ->
            cameraViewModelState.copy(
                error = error
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun uploadFile(file: File){

        //Log.d("CameraViewModel", "Try to upload file ${file.name}")
        error("")

        //Use e IO disptcher to have more threads for streaming file. Otherwise android.os.NetworkOnMainThreadException occurred
        viewModelScope.launch(Dispatchers.IO) {

            //Create file part
            val imagePart = MultipartBody.Part
                .createFormData(
                    name = "image",
                    filename = file.name,
                    body = file.asRequestBody()
                )

            //Create part contains additional info
            val infoPart = MultipartBody.Part
                .createFormData(
                    "timestamp",
                    System. currentTimeMillis().toString()
                )

            //Log.d("CameraViewModel", "file space: ${fileSize(file)} Byte")
            loading(true)
            //Get response
            try {

                val totalTimeElapse = measureTimeMillis {

                    var response: ResponseBody? = null
                    val timeElapsedDetect = measureTimeMillis {
                        response = repository.retrofitService.uploadFileAndDetect(
                            imagePart,
                            infoPart
                        )
                    }
                    loading(false)
                    //Log.d("CameraViewModel", "Analyze image in: $timeElapsedDetect ms")

                    //Save new file
                    val timeElapsedSaveFile = measureTimeMillis {
                        response!!.saveFile()
                    }
                    //Log.d("CameraViewModel", "Save image in: $timeElapsedSaveFile ms")
                }

                //Current frame rate [FPS]
                setFrameRate(round(1/(totalTimeElapse/1000F)))

            }catch (ex: Exception){

                loading(false)
                _mainState.update { cameraViewModelState ->
                    cameraViewModelState.copy(
                        error = ex.toString()
                    )
                }
                Log.e("CameraViewModel", ex.toString())
            }
        }
    }

    //Support file state
    fun setFile(file: File){
        _mainState.update { cameraViewModelState ->
            cameraViewModelState.copy(
                file = file
            )
        }
    }

    //Original file
    fun setFileOrigin(file: File){
        _mainState.update { cameraViewModelState ->
            cameraViewModelState.copy(
                fileOrigin = file
            )
        }
    }

    //Save external cache dir in state to reuse it
    fun setExternalDir(externalDir: File){
        _mainState.update { cameraViewModelState ->
            cameraViewModelState.copy(
                externalCacheDir = externalDir
            )
        }
    }

    //set image bitmap
    private fun setImageBitmap(image : Bitmap){
        _mainState.update { cameraViewModelState ->
            cameraViewModelState.copy(
                imgBitmap = image
            )
        }
    }

    //Set frame rate
    private fun setFrameRate(frameRate: Float){
        _mainState.update { cameraViewModelState ->
            cameraViewModelState.copy(
                frameRate = frameRate
            )
        }
    }

    private fun setResponseDimension(byte: Int){
        _mainState.update { cameraViewModelState ->
            cameraViewModelState.copy(
                responseDimension = byte
            )
        }
    }

    //Create a file into external cache dir. Used to store original file and detected file
    fun createImageFile(name : String): File {
        // Create an image file name
        //val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        //val imageFileName = "JPEG_" + timeStamp + "_"
        val imageFileName = "JPEG_" + name + "_"
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            _mainState.value.externalCacheDir      /* directory */
        )

        return image
    }

    //Handle byte array download and save them int file download like a streaming and save it into file
    private fun ResponseBody.saveFile() {

        var bitmap : Bitmap?? = null
        byteStream().use { inputStream ->
            bitmap = BitmapFactory.decodeStream(inputStream)
        }
        setImageBitmap(bitmap!!)

        /*val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 90, stream)
        setResponseDimension(stream.toByteArray().size)
         */
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fileSize(file: File): Long {
        val attributes: BasicFileAttributes =
            Files.readAttributes(file.toPath(), BasicFileAttributes::class.java)
        return attributes.size()
    }
}
