package com.kudigo.mobile_money_util

interface PaymentInterface {
    fun onSuccess(network: String, number:String)
    fun onFailed(reason: String)
    fun onCancelled()

}
