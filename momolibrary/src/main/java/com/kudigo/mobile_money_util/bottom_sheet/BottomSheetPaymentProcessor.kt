package com.kudigo.mobile_money_util.bottom_sheet

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.kudigo.mobile_money_util.*
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

        })

        builder.setPositiveButton("RETRY") { dialog, which ->

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
        val context = this
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(title)
        builder.setMessage(message)

        val view = layoutInflater.inflate(R.layout.dialog_accept_input, null)

        val editText = view.findViewById(R.id.edittextInput) as EditText
        editText.setHint(hint)

        builder.setView(view);

        builder.setPositiveButton("Done") { dialog, p1 ->
            val textValue = editText.text.toString()

        }

        builder.setNegativeButton(android.R.string.cancel) { dialog, p1 ->
            dialog.cancel()
        }

        builder.show();
    }


    private fun transactionFinished() {
        dismiss()

        paymentCallbackInterface?.onSuccess(paymentInfo!!.network, paymentInfo!!.number)

    }

    private fun cancelTransaction() {
        dismiss()
    }


    // transaction failed
    private fun transactionFailed(message: String) {
        buttonOptions.visibility = View.VISIBLE
        buttonCancel.visibility = View.VISIBLE
        paymentProgress.visibility = View.GONE
        textViewMessage.text = message
        textViewMessage.setTextColor(requireActivity().resources!!.getColor(R.color.colorRed))
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