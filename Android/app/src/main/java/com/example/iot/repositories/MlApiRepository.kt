package com.example.iot.repositories

import com.example.iot.apiAccess.RetrofitService
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming
import javax.inject.Inject

class MlApiRepository @Inject constructor(
    private val networkService: RetrofitService
) {
    //To access to localhost API use 10.0.2.2.
    //Further infos here https://stackoverflow.com/questions/4905315/error-connection-refused
    private val BASE_URL = "http://10.0.2.2:5000/"//""http://10.0.2.2:5000/"

    //Retrofit builder
    private val retrofit = networkService.buildRetrofit(BASE_URL)

    //Used in view model to invoke repository. MlApiRepository.retrofitService.getDeviceInfo
    val retrofitService : MlApiService by lazy {
        retrofit.create(MlApiService::class.java)
    }


}

interface MlApiService{

    //Use streaming to download chunks of file without drain the memory
    @Streaming
    @Multipart
    @POST("/yolo/object-detection")
    suspend fun uploadFileAndDetect(
        @Part files: MultipartBody.Part,
        @Part info: MultipartBody.Part
    ): ResponseBody
}