package com.kudigo.mobile_money_util

import androidx.appcompat.app.AppCompatActivity

class MakePayment(private var context: AppCompatActivity) {
    private var amount: Double? = null
    private var callBack: PaymentCallbackInterface? = null
    private var tag = "PAYMENT_HANDLER"

    // Show MOMO processor
    fun startMoMoPaymentProcessor(paymentInfo: PaymentInfo, amount: Double, paymentCallbackInterface: PaymentCallbackInterface) {
        this.amount = amount
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