package com.kudigo.mobile_money_util

import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MakePayment(var context: AppCompatActivity) {
    private var amount: Double? = null
    private var callBack: PaymentCallbackInterface? = null
    private var tag = "PAYMENT_HANDLER"

    // Show MOMO processor
    fun showMoMoPaymentProcessor(paymentInfo: PaymentInfo, amount: Double,connectionError:String,paymentCallbackInterface: PaymentCallbackInterface) {
        this.amount = amount
        this.callBack = paymentCallbackInterface

        // MARK: internet connection not available
        if (!Utility().hasNetworkConnection(context.applicationContext)) {
            callBack?.onReceivedError(connectionError)
            return
        }

        paymentRequest(paymentInfo)

        val bottomSheetMoMoPaymentStatus = BottomSheetPaymentProcessor.newInstance(context, amount, paymentInfo, paymentCallbackInterface)
        bottomSheetMoMoPaymentStatus.isCancelable = false
        bottomSheetMoMoPaymentStatus.show(context.supportFragmentManager, tag)
    }

    fun paymentRequest(paymentInfo: PaymentInfo) {
        val retrofit = ServiceBuilder.buildService(ApiUrls::class.java)
        retrofit.paymentRequest(paymentInfo).enqueue(
                object : Callback<PaymentInfo> {
                    override fun onFailure(call: Call<PaymentInfo>, t: Throwable) {
                        Toast.makeText(context,"" + t,Toast.LENGTH_SHORT).show()

                    }

                    override fun onResponse(call: Call<PaymentInfo>, response: Response<PaymentInfo>) {
                        val result = response.body()
                        Toast.makeText(context,"" + result,Toast.LENGTH_SHORT).show()
                    }
                }
        )
    }


}