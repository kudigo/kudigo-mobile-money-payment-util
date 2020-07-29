package com.kudigo.mobile_money_util.retrofit

import com.kudigo.mobile_money_util.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()
    private val BASE_URL = BuildConfig.BASE_URL

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL) // change
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}