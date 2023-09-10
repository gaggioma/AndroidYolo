package com.example.iot.apiAccess

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class RetrofitService @Inject constructor() {

    private fun createOkHttpClientShipSSL(): OkHttpClient {

        return try {

            val trustAllCerts: Array<TrustManager> = arrayOf(MyManager())
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, SecureRandom())

            //No interceptor
            OkHttpClient.Builder()
            .readTimeout(60, TimeUnit.SECONDS) //Timeout
            .connectTimeout(60, TimeUnit.SECONDS) //Timeout
            /*.sslSocketFactory(
                sslContext.getSocketFactory(),
                trustAllCerts[0] as X509TrustManager
            )
            .hostnameVerifier { hostname: String?, session: SSLSession? -> true }
             */
            .build()


        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    //Retrofit builder with basic interceptor
    fun buildRetrofit(BASE_URL: String): Retrofit {

        Log.d("RetrofitService", "buildRetrofit $BASE_URL")

        //Custom json converter
        val gson : Gson = GsonBuilder()
            .serializeNulls()
            .setLenient()
            .create()

        //Interceptor
        val okHttpClient: OkHttpClient = createOkHttpClientShipSSL()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(BASE_URL)
            .build()
    }
}

//Verify SSL manager
class MyManager : X509TrustManager {

    override fun checkServerTrusted(
        p0: Array<out java.security.cert.X509Certificate>?,
        p1: String?
    ) {
        //allow all
    }

    override fun checkClientTrusted(
        p0: Array<out java.security.cert.X509Certificate>?,
        p1: String?
    ) {
        //allow all
    }

    override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
        return arrayOf()
    }
}