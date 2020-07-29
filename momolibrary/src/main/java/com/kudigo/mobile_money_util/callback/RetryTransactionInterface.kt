package com.kudigo.mobile_money_util.callback

import com.kudigo.mobile_money_util.data.MoMoPaymentInfo

interface RetryTransactionInterface : PaymentCallbackInterface{

    fun onReceivedDetail(message:String)
    fun onReceivedDone()

}
