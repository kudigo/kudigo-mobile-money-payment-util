package com.kudigo.mobile_money_util.data

import com.google.gson.annotations.SerializedName


open class MoMoPaymentInfo(
    @SerializedName("id")
    var id: String = "",
    @SerializedName("name")
    var name: String = "",
    @SerializedName("network")
    var network: String = "",
    @SerializedName("number")
    var number: String = "",
    @SerializedName("reference")
    var reference: String = "",
    @SerializedName("status")
    var status: String = "",
    @SerializedName("vouchercode")
    var voucherCode: String = "",
    @SerializedName("amount")
    var amountPaid: Double = 0.0
)