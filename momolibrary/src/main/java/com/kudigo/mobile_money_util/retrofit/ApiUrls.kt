package com.kudigo.mobile_money_util.retrofit

import com.kudigo.mobile_money_util.data.MoMoPaymentInfo
import com.kudigo.mobile_money_util.data.TransactionItem
import retrofit2.Call
import retrofit2.http.*

interface ApiUrls{

    @Headers("Content-Type: application/json")
    @POST("payment")
    fun paymentRequest(@Body paymentInfo: MoMoPaymentInfo): Call<MoMoPaymentInfo>
    
    @GET("payment_status")
    fun checkPaymentStatus(@Query("transactionId") transactionId:String): Call<TransactionItem>
}