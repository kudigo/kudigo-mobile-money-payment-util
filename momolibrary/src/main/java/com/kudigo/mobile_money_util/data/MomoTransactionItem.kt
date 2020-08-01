package com.kudigo.mobile_money_util.data

import com.google.gson.annotations.SerializedName

data class MomoTransactionItem(
        @SerializedName("id")
        val id: String,
        @SerializedName("time_Created")
        val timeCreated: String,
        @SerializedName("time_Completed")
        val timeCompleted: String,
        @SerializedName("debit_Amt")
        val debitAmt: String,
        @SerializedName("credit_Amt")
        val creditAmt: String,
        @SerializedName("source")
        val source: String,
        @SerializedName("destination")
        var destination: String,
        @SerializedName("transaction_Status")
        var transactionStatus: String,
        @SerializedName("description")
        var description: String,
        @SerializedName("platform")
        var platform: String,
        @SerializedName("sender_Wallet_Number")
        var senderWalletNumber: String,
        @SerializedName("recipient_Wallet_Number")
        var recipientWalletNumber: String,
        @SerializedName("total_Charge")
        var totalCharge: String,
        @SerializedName("transaction_Id")
        var transactionId: String
)