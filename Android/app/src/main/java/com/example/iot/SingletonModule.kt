package com.example.iot

import com.example.iot.apiAccess.RetrofitService
import com.example.iot.repositories.IotApiRepository
import com.example.iot.repositories.MlApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SingletonModule {

    //Retrofit service
    @Singleton
    @Provides
    fun provideRetrofitService() : RetrofitService = RetrofitService()

    //API repository
    @Singleton
    @Provides
    fun provideIotApiRepository(networkService : RetrofitService) : IotApiRepository = IotApiRepository(networkService = networkService)

    //API repository
    @Singleton
    @Provides
    fun provideMlApiRepository(networkService : RetrofitService) : MlApiRepository = MlApiRepository(networkService = networkService)

}