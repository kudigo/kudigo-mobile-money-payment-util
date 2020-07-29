package com.kudigo.mobile_money_util

import com.google.gson.annotations.SerializedName

data class TransactionItem(
        @SerializedName("id")
        val id: String,
        @SerializedName("timeCreated")
        val timeCreated: String,
        @SerializedName("timeCompleted")
        val timeCompleted: String,
        @SerializedName("debitAmt")
        val debitAmt: String,
        @SerializedName("creditAmt")
        val creditAmt: String,
        @SerializedName("source")
        val source: String,
        @SerializedName("destination")
        var destination: String,
        @SerializedName("transactionStatus")
        var transactionStatus: String,
        @SerializedName("description")
        var description: String,
        @SerializedName("platform")
        var platform: String,
        @SerializedName("senderWalletNumber")
        var senderWalletNumber: String,
        @SerializedName("recipientWalletNumber")
        var recipientWalletNumber: String,
        @SerializedName("totalCharge")
        var totalCharge: String,
        @SerializedName("transactionId")
        var transactionId: String
)