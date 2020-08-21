package com.kudigo.mobile_money_util.retrofit

import com.kudigo.mobile_money_util.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    private const val BASE_URL = "http://api.server.com/"
    private const val BASE_TEST_URL = "http://api.server.com/"


    private val authInterceptor = Interceptor {chain->
        val newUrl = chain.request().url()
                .newBuilder()
                .build()

        val newRequest = chain.request()
                .newBuilder()
                .url(newUrl)
                .build()

        chain.proceed(newRequest)
    }

    private val client = OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl(if (BuildConfig.DEBUG) BASE_TEST_URL else BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()


    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }


}
