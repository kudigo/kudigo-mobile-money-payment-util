package com.kudigo.mobile_money_util.data

import com.google.gson.annotations.SerializedName

data class TransactionId(
        @SerializedName("frontend_order_id")
        var orderId: String = "",
        @SerializedName("momo_transaction_id")
        var momoTransactionId: String = ""

)
