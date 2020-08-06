package com.kudigo.mobile_money_util.data

import com.google.gson.annotations.SerializedName

data class MomoTransactionItem(
        @SerializedName("id")
        val id: String,
        @SerializedName("time_created")
        val timeCreated: String,
        @SerializedName("time_completed")
        val timeCompleted: String,
        @SerializedName("debit_amt")
        val debitAmt: String,
        @SerializedName("credit_amt")
        val creditAmt: String,
        @SerializedName("source")
        val source: String,
        @SerializedName("destination")
        var destination: String,
        @SerializedName("transaction_status")
        var transactionStatus: String,
        @SerializedName("description")
        var description: String,
        @SerializedName("platform")
        var platform: String,
        @SerializedName("sender_wallet_number")
        var senderWalletNumber: String,
        @SerializedName("recipient_wallet_number")
        var recipientWalletNumber: String,
        @SerializedName("total_charge")
        var totalCharge: String,
        @SerializedName("transaction_id")
        var transactionId: String
)

data class TransactionResult(
        @SerializedName("results")
        var results:MomoTransactionItem
)