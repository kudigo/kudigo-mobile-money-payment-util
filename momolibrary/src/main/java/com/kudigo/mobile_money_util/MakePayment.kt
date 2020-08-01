package com.kudigo.mobile_money_util

import androidx.appcompat.app.AppCompatActivity
import com.kudigo.mobile_money_util.bottom_sheet.BottomSheetPaymentProcessor
import com.kudigo.mobile_money_util.callback.MoMoPaymentCallbackInterface
import com.kudigo.mobile_money_util.data.MoMoPaymentExtraInfo
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo


class MakePayment(private var context: AppCompatActivity) {
    private var callBack: MoMoPaymentCallbackInterface? = null
    private var tag = "PAYMENT_HANDLER"

    //start momo processor
    fun startMoMoPaymentProcessor(paymentInfo: MoMoPaymentInfo, paymentExtraInfo: MoMoPaymentExtraInfo? = null, paymentCallbackInterface: MoMoPaymentCallbackInterface) {
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