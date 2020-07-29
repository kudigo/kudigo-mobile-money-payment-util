package com.kudigo.mobile_money_util.callback

interface PaymentCallbackInterface {
    fun onSuccess(network: String, number:String)
    fun onFailed(reason: String)
    fun onCancelled()
    fun onReceivedError(message: String)

}
