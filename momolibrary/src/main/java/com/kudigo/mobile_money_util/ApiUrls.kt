package com.kudigo.mobile_money_util

import retrofit2.Call
import retrofit2.http.*

interface ApiUrls{

    @Headers("Content-Type: application/json")
    @POST("payment")
    fun paymentRequest(@Body paymentInfo: PaymentInfo): Call<PaymentInfo>
    
    @GET("payment_status")
    fun checkPaymentStatus(@Query("transactionId") transactionId:String): Call<TransactionItem>
}