package com.kudigo.mobile_money_util.retrofit

import com.kudigo.mobile_money_util.data.*
import retrofit2.Call
import retrofit2.http.*

interface ApiUrls {
    @Headers("Content-Type: application/json")
    @POST("retry_momo_transaction/")
    fun paymentRequest(@Body paymentInfo: MoMoPaymentInfo, @Header("Authorization") token: String): Call<MoMoPaymentInfo>

    @GET("transaction_tariffs/")
    fun getMomoCharges( @Header("Authorization") token: String): Call<JsonArrayResponse>

    @POST("check_momo_status/")
    fun checkPaymentStatus(@Body transactionId: TransactionId, @Header("Authorization") token: String): Call<TransactionResult>
}