package com.kudigo.mobile_money_util.data

import com.google.gson.annotations.SerializedName

data class MomoCharge(
        @SerializedName("id")
        var id: String = "",
        @SerializedName("min_transaction_amount")
        var lowerBound: Double = 0.0,
        @SerializedName("max_transaction_amount")
        var upperBound: Double = 0.0,
        @SerializedName("charge_type")
        var chargeType: String = "",
        @SerializedName("charge_value")
        var chargeValue: Double = 0.0
)

class JsonArrayResponse {
   @SerializedName("results")
    var results: MutableList<MomoCharge>? = null

}

