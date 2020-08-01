package com.kudigo.mobile_money_util.data

import com.google.gson.annotations.SerializedName


open class MoMoPaymentInfo(
    @SerializedName("frontend_order_id")
    var id: String = "",
    @SerializedName("payment_network")
    var network: String = "",
    @SerializedName("customer_number")
    var number: String = "",
    @SerializedName("payment_voucher-code")
    var voucherCode: String = "",
    @SerializedName("payment_status")
    var status: String = ""

)