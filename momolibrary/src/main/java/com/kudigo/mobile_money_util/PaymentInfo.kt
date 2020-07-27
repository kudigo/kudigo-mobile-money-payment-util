package com.kudigo.mobile_money_util

open class PaymentInfo(
    var id: String = "",
    var name: String = "",
    var network: String = "",
    var number: String = "",
    var reference: String = "",
    var status: String = "",
    var voucherCode: String = "",
    var amountPaid: Double = 0.0

)