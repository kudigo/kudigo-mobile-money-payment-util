package com.kudigo.mobile_money_util

import androidx.appcompat.app.AppCompatActivity
import com.kudigo.mobile_money_util.bottom_sheet.BottomSheetPaymentProcessor
import com.kudigo.mobile_money_util.callback.MoMoPaymentCallbackInterface
import com.kudigo.mobile_money_util.data.MoMoPaymentExtraInfo
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo


class MakePayment(private var context: AppCompatActivity) {
    private var tag = "PAYMENT_HANDLER"
    private var apiToken = ""

    //set api token
    fun setApiToken(apiToken: String): MakePayment {
        this.apiToken = apiToken
        return this
    }

    //start momo processor
    fun startMoMoPaymentProcessor(paymentInfo: MoMoPaymentInfo, paymentExtraInfo: MoMoPaymentExtraInfo? = null, paymentCallbackInterface: MoMoPaymentCallbackInterface) {
        val bottomSheetMoMoPaymentStatus = BottomSheetPaymentProcessor.newInstance(context, apiToken, paymentInfo, paymentCallbackInterface)
        bottomSheetMoMoPaymentStatus.isCancelable = false
        bottomSheetMoMoPaymentStatus.show(context.supportFragmentManager, tag)
    }

}