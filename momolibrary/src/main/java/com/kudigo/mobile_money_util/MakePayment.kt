package com.kudigo.mobile_money_util

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.kudigo.mobile_money_util.bottom_sheet.BottomSheetPaymentProcessor
import com.kudigo.mobile_money_util.callback.PaymentCallbackInterface
import com.kudigo.mobile_money_util.data.MoMoPaymentExtraInfo
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo
import com.kudigo.mobile_money_util.retrofit.ApiUrls
import com.kudigo.mobile_money_util.retrofit.ServiceBuilder


class MakePayment(private var context: AppCompatActivity) {
    private var callBack: PaymentCallbackInterface? = null
    private var tag = "PAYMENT_HANDLER"

    //start momo processor
    fun startMoMoPaymentProcessor(paymentInfo: MoMoPaymentInfo, paymentExtraInfo: MoMoPaymentExtraInfo? = null, paymentCallbackInterface: PaymentCallbackInterface) {
        this.callBack = paymentCallbackInterface

        // MARK: internet connection not available
        if (!Utility().hasNetworkConnection(context.applicationContext)) {
            callBack?.onReceivedError(paymentExtraInfo!!.errorMessage)
            return
        }

        val bottomSheetMoMoPaymentStatus = BottomSheetPaymentProcessor.newInstance(context, paymentInfo, paymentCallbackInterface)
        bottomSheetMoMoPaymentStatus.isCancelable = false
        bottomSheetMoMoPaymentStatus.show(context.supportFragmentManager, tag)
    }




}