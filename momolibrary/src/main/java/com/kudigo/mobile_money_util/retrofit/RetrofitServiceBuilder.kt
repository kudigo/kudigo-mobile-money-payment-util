package com.kudigo.mobile_money_util.retrofit

import com.kudigo.mobile_money_util.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ServiceBuilder {
    private val BASE_URL = BuildConfig.BASE_URL
    private val API_KEY= BuildConfig.API_KEY


    private val authInterceptor = Interceptor {chain->
        val newUrl = chain.request().url()
                .newBuilder()
                .addQueryParameter("api_key", API_KEY)
                .build()

        val newRequest = chain.request()
                .newBuilder()
                .url(newUrl)
                .build()

        chain.proceed(newRequest)
    }

    //OkhttpClient for building http request url
    private val client = OkHttpClient().newBuilder()
            .addInterceptor(authInterceptor)
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // change
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()


    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }




}