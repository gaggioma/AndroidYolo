package com.example.iot.repositories

import com.example.iot.apiAccess.RetrofitService
import com.example.iot.repositories.model.DeviceInfoModel
import retrofit2.http.GET
import javax.inject.Inject

class IotApiRepository @Inject constructor(
    private val networkService: RetrofitService
) {

    private val BASE_URL_KPI = "http://192.168.4.1:6711"

    //Retrofit builder
    private val retrofit = networkService.buildRetrofit(BASE_URL_KPI)

    //Used in view model to invoke repository. IotApiRepository.retrofitService.getDeviceInfo
    val retrofitService : IotApiService by lazy {
        retrofit.create(IotApiService::class.java)
    }


}

interface IotApiService{

    @GET("/")
    suspend fun getDeviceInfo(): DeviceInfoModel

}