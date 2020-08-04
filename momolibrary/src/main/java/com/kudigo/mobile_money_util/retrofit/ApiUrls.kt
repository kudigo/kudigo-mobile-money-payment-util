package com.kudigo.mobile_money_util.retrofit

import com.kudigo.mobile_money_util.data.JsonArrayResponse
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo
import com.kudigo.mobile_money_util.data.MomoCharge
import com.kudigo.mobile_money_util.data.MomoTransactionItem
import retrofit2.Call
import retrofit2.http.*

interface ApiUrls {

    @Headers("Content-Type: application/json")
    @POST("retry_momo_transaction/")
    fun paymentRequest(@Body paymentInfo: MoMoPaymentInfo, @Header("Authorization") token: String): Call<MoMoPaymentInfo>

    @GET("transaction_tariffs/")
    fun getMomoCharges( @Header("Authorization") token: String): Call<JsonArrayResponse>

    @GET("check_momo_status/")
    fun checkPaymentStatus(@Query("transactionId") transactionId: String, @Header("Authorization") token: String): Call<MomoTransactionItem>
}