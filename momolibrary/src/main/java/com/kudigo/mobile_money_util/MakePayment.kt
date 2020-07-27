package com.kudigo.mobile_money_util

import androidx.appcompat.app.AppCompatActivity
import com.kudigo.mobile_money_util.bottom_sheet.BottomSheetPaymentProcessor
import com.kudigo.mobile_money_util.callback.PaymentCallbackInterface
import com.kudigo.mobile_money_util.data.MoMoPaymentExtraInfo
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo

class MakePayment(private var context: AppCompatActivity) {
    private var callBack: PaymentCallbackInterface? = null
    private var tag = "PAYMENT_HANDLER"

    // Show MOMO processor
    fun startMoMoPaymentProcessor(paymentInfo: MoMoPaymentInfo, paymentExtraInfo: MoMoPaymentExtraInfo? = null, paymentCallbackInterface: PaymentCallbackInterface) {
        this.callBack = paymentCallbackInterface

        // MARK: internet connection not available
        if (!Utility().hasNetworkConnection(context.applicationContext)) {
            callBack?.onReceivedError("Cannot process payment because you are offline")
            return
        }

        val bottomSheetMoMoPaymentStatus = BottomSheetPaymentProcessor.newInstance(context, paymentInfo, paymentCallbackInterface)
        bottomSheetMoMoPaymentStatus.isCancelable = false
        bottomSheetMoMoPaymentStatus.show(context.supportFragmentManager, tag)
    }

}