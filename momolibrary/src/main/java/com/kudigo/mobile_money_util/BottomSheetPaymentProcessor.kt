package com.kudigo.mobile_money_util

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bottom_sheet_payment_processor.*

class BottomSheetPaymentProcessor : RoundedBottomSheetDialogFragment() {

    private var paymentCallbackInterface: PaymentCallbackInterface? = null
    private var paymentInfo: PaymentInfo? = null
    private var paymentInterface: PaymentCallbackInterface? = null
    private var activityCalling: Activity? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_payment_processor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        buttonMobileMoneyAction.setOnClickListener {
            transactionFinished()
        }
        buttonCancel.setOnClickListener {
            cancelTransaction()
        }
        buttonOptions.setOnClickListener {
            changeNetwork()
        }

    }

    fun paymentRequest(paymentInfo: PaymentInfo) {
        //do request here
    }

    private fun changeNetwork() {
        //retry with another network

    }

    private fun transactionFinished() {
        dismiss()

        paymentCallbackInterface?.onSuccess(paymentInfo!!.network, paymentInfo!!.number)

    }

    private fun cancelTransaction() {
        dismiss()
    }


    // transaction failed
    private fun transactionFailed() {
        buttonOptions.visibility = View.VISIBLE
        buttonCancel.visibility = View.VISIBLE
        paymentProgress.visibility = View.GONE
        textViewMessage.text = "Transaction failed"
        textViewMessage.setTextColor(activity!!.resources!!.getColor(R.color.colorRed))
    }


    companion object {
        fun newInstance(activity: Activity, paymentInfo: PaymentInfo? = null, callback: PaymentCallbackInterface) =
            BottomSheetPaymentProcessor().apply {
                this.activityCalling = activity
                this.paymentInfo = paymentInfo
                this.paymentInterface = callback
            }
    }


}