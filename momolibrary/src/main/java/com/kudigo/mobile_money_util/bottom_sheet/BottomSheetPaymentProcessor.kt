package com.kudigo.mobile_money_util.bottom_sheet

import android.R
import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.kudigo.mobile_money_util.PaymentStatus
import com.kudigo.mobile_money_util.callback.PaymentCallbackInterface
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo
import com.kudigo.mobile_money_util.data.TransactionItem
import com.kudigo.mobile_money_util.retrofit.ApiUrls
import com.kudigo.mobile_money_util.retrofit.ServiceBuilder
import kotlinx.android.synthetic.main.bottom_sheet_payment_processor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BottomSheetPaymentProcessor : RoundedBottomSheetDialogFragment() {

    private var paymentCallbackInterface: PaymentCallbackInterface? = null
    private var paymentInfo: MoMoPaymentInfo? = null
    private var paymentInterface: PaymentCallbackInterface? = null
    private var activityCalling: Activity? = null
    private val networkOptions = arrayOf("MTN", "VODAFONE", "AIRTEL", "TIGO")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_payment_processor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkPaymentStatus()

        buttonMobileMoneyAction.setOnClickListener {
            transactionFinished()

        }
        buttonCancel.setOnClickListener {
            cancelTransaction()
        }
        buttonChange.setOnClickListener {
            changeNetwork()
        }

        buttonOptions.setOnClickListener {
            retryMomoTransaction(paymentInfo!!)
        }

    }


    fun checkPaymentStatus(){
        val retrofit = ServiceBuilder.buildService(ApiUrls::class.java)
        retrofit.checkPaymentStatus(paymentInfo!!.id).enqueue(
                object : Callback<TransactionItem> {
                    override fun onFailure(call: Call<TransactionItem>, t: Throwable) {
                        transactionFailed(t.toString())

                    }

                    override fun onResponse(call: Call<TransactionItem>, response: Response<TransactionItem>) {
                        val result = response.body()
                        if(response.body()?.transactionStatus==PaymentStatus.SUCCESS.name){
                            paymentInfo?.status= PaymentStatus.SUCCESS.name
                            buttonOptions.visibility = View.GONE
                            paymentProgress.visibility = View.GONE
                            textViewMessage.text = "Transaction successful"
                            textViewMessage.setTextColor(activity!!.resources!!.getColor(R.color.colorPrimary))
                        }
                        Toast.makeText(context,"" + result,Toast.LENGTH_SHORT).show()
                    }
                }
        )
    }


    //retry with another network
    fun changeNetwork() {
        var selectedOption = 0
        val builder = AlertDialog.Builder(activityCalling!!)
        builder.setTitle("Choose Another Network")
        builder.setSingleChoiceItems(networkOptions, selectedOption, DialogInterface.OnClickListener { dialog, which ->
            selectedOption = which
            Toast.makeText(activityCalling, networkOptions[which], Toast.LENGTH_SHORT).show()
            enterNumber()

        })

        builder.setPositiveButton("Continue") { dialog, which ->

            dialog.dismiss()
        }
        builder.show()
        dismiss()

    }

    //enter number to retry
    fun enterNumber() {
        acceptInputDialog("Retry Transaction", "Enter customer number or different number to try again.", "Customer Phone Number")


    }


    //FIX
    fun acceptInputDialog(title: String, message: String, hint: String) {
        MaterialDialog(activityCalling!!).show {
            title(text=title)
            message(text=message)
            input(hint= hint,waitForPositiveButton = false) { dialog, text ->
                // Text changed
            }
            positiveButton(R.string.done)
        }

    }


    private fun transactionFinished() {
        dismiss()
        paymentCallbackInterface?.onSuccess(paymentInfo!!.network, paymentInfo!!.number)

    }

    private fun cancelTransaction() {
        dismiss()
        paymentCallbackInterface?.onReceivedData(paymentInfo!!, "Transaction cancelled")
    }


    // transaction failed
    private fun transactionFailed(message: String) {
        buttonOptions.visibility = View.VISIBLE
        buttonCancel.visibility = View.VISIBLE
        paymentProgress.visibility = View.GONE
        textViewMessage.text = message
        textViewMessage.setTextColor(requireActivity().resources!!.getColor(R.color.colorRed))
    }

    private fun retryMomoTransaction(paymentInfo: MoMoPaymentInfo){
        val retrofit = ServiceBuilder.buildService(ApiUrls::class.java)
        retrofit.retryPayment(paymentInfo).enqueue(
                object : Callback<MoMoPaymentInfo> {
                    override fun onFailure(call: Call<MoMoPaymentInfo>, t: Throwable) {
                        Toast.makeText(context,"" + t,Toast.LENGTH_SHORT).show()

                        paymentProgress.visibility = View.GONE
                        textViewMessage.text = t.toString()
                        buttonOptions.visibility = View.VISIBLE
                    }

                    override fun onResponse(call: Call<MoMoPaymentInfo>, response: Response<MoMoPaymentInfo>) {
                        val result = response.body()
                        Toast.makeText(context,"" + result,Toast.LENGTH_SHORT).show()

                        paymentProgress.visibility = View.GONE
                        textViewMessage.text = response.message()
                        buttonOptions.visibility = View.VISIBLE
                        buttonMobileMoneyAction.visibility = View.VISIBLE


                    }
                }
        )
    }

    companion object {
        fun newInstance(activity: Activity, paymentInfo: MoMoPaymentInfo? = null, callback: PaymentCallbackInterface) =
            BottomSheetPaymentProcessor().apply {
                this.activityCalling = activity
                this.paymentInfo = paymentInfo
                this.paymentInterface = callback
            }
    }


}