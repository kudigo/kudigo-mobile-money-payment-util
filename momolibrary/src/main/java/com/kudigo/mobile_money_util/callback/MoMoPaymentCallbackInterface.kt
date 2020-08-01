package com.kudigo.mobile_money_util.callback

import com.kudigo.mobile_money_util.data.MoMoPaymentInfo

interface MoMoPaymentCallbackInterface {
    fun onSuccess(network: String, number:String)
    fun onFailed(reason: String)
    fun onCancelled()
    fun onReceivedError(errorMessage: String)
    fun onReceivedData(moMoPaymentInfo: MoMoPaymentInfo, cancelMessage:String)
}
