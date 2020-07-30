package com.kudigo.mobile_money_util.bottom_sheet

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.kudigo.mobile_money_util.PaymentNetworks
import com.kudigo.mobile_money_util.PaymentStatus
import com.kudigo.mobile_money_util.R
import com.kudigo.mobile_money_util.Utility
import com.kudigo.mobile_money_util.callback.PaymentCallbackInterface
import com.kudigo.mobile_money_util.callback.MomoResultInterface
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
    private var timer: CountDownTimer? = null
    private var time = 2000000L
    private var amount: Double = 0.00
    //private val networkOptions = arrayListOf<String>()
    private val networkOptions = arrayOf(PaymentNetworks.MTN.name,PaymentNetworks.VODAFONE.name,PaymentNetworks.AIRTEL.name,PaymentNetworks.TIGO.name)
    private val retrofit = ServiceBuilder.buildService(ApiUrls::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_payment_processor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val orderLabel = activityCalling!!.getString(R.string.order_amount_charge)
        val amountRounded = Utility().round(amount, 2)
        textViewOrderLabel.text = orderLabel


        updatePaymentStatus()
        paymentRequest(paymentInfo!!)
        showPaymentIcon()

        buttonMobileMoneyAction.setOnClickListener {
            transactionFinished()
            cancelTimerAction()

        }
        buttonCancel.setOnClickListener {
            cancelTransaction()
        }
        buttonChange.setOnClickListener {
            changeNetwork()
        }

        buttonOptions.setOnClickListener {
            cancelTimerAction()
            retryMomoTransaction(paymentInfo!!)
        }


        paymentInfo?.let {
            paymentProgress.visibility = View.VISIBLE
            retryMomoTransaction(it)
        }

    }


    fun checkPaymentStatus() {
        retrofit.checkPaymentStatus(paymentInfo!!.id).enqueue(
            object : Callback<TransactionItem> {
                override fun onFailure(call: Call<TransactionItem>, t: Throwable) {
                    transactionFailed(t.toString())
                }

                override fun onResponse(call: Call<TransactionItem>, response: Response<TransactionItem>) {
                    val result = response.body()
                    if (response.body()?.transactionStatus == PaymentStatus.SUCCESS.name) {
                        paymentInfo?.status = PaymentStatus.SUCCESS.name
                        buttonOptions.visibility = View.GONE
                        paymentProgress.visibility = View.GONE
                        textViewMessage.text = getString(R.string.transaction_successful)
                        textViewMessage.setTextColor(activity!!.resources!!.getColor(R.color.colorPrimary))
                    }
                   // c
                }
            }
        )
    }


    //retry with another network
    private fun changeNetwork() {
        var selectedOption = 0
        val builder = AlertDialog.Builder(activityCalling!!)
        builder.setTitle(getString(R.string.continue_with_another_network))
        builder.setSingleChoiceItems(networkOptions, selectedOption, DialogInterface.OnClickListener { dialog, which ->
            selectedOption = which
            paymentInfo!!.network = networkOptions[selectedOption]

        })

        builder.setPositiveButton(getString(R.string.continue_)) { dialog, which ->

            enterNumber()
           showPaymentIcon()
            dialog.dismiss()
        }
        builder.show()
        dismiss()

    }

    //enter number to retry
    private fun enterNumber() {
        var placeHolder = paymentInfo!!.number
        if (placeHolder.startsWith("+233")) {
            placeHolder = placeHolder.replace("+233", "0")
        }
        acceptInputDialog(activityCalling!!.getString(R.string.retry_payment_transaction),
                activityCalling!!.getString(R.string.enter_customer_number_or_different_number),
                activityCalling!!.getString(R.string.customer_phone_number),
        placeHolder,
        object :MomoResultInterface{
            override fun onReceivedData(data: String) {
                paymentInfo!!.number = data
                showPaymentIcon()
                if (paymentInfo!!.network == PaymentNetworks.VODAFONE.name) {
                    enterVodafoneVoucher()
                    return
                }
                retryMomoTransaction(paymentInfo!!)
            }
        })
    }


    private fun enterVodafoneVoucher() {
        acceptInputDialog(activityCalling!!.getString(R.string.retry_payment_transaction),
                activityCalling!!.getString(R.string.enter_voucher_code_vodafone_cash),
                activityCalling!!.getString(R.string.voucher_code),
        "",
        object :MomoResultInterface{
            override fun onReceivedData(data: String) {
                paymentInfo!!.voucherCode = data
                retryMomoTransaction(paymentInfo!!)
            }

        })
    }

    //dialog to accept input
    private fun acceptInputDialog(title: String, message: String, hint: String, defaultInput:String? = "",resultInterface: MomoResultInterface) {
        MaterialDialog.Builder(activityCalling!!)
                .title(title)
                .typeface(ResourcesCompat.getFont(activityCalling!!, R.font.brown_regular), ResourcesCompat.getFont(activityCalling!!, R.font.brown_thin))
                .cancelable(true)
                .contentLineSpacing(1.2f)
                .buttonRippleColor(ContextCompat.getColor(activityCalling!!, R.color.colorPrimaryDark))
                .positiveColor(ContextCompat.getColor(activityCalling!!, R.color.colorPrimaryDark))
                .negativeColor(ContextCompat.getColor(activityCalling!!, R.color.colorPrimary))
                .neutralColor(ContextCompat.getColor(activityCalling!!, R.color.colorLightDark))
                .content(message)
                .positiveText(activityCalling!!.getString(R.string.done))
                .input(hint, defaultInput, false) { dialog, input ->
                    dialog.dismiss()
                    resultInterface.onReceivedData(input.trim().toString())
                }
                .onPositive { dialog, _ ->
                    dialog.dismiss()
                }
                .show()

    }



    //on failure
    private fun transactionFinished() {
        dismiss()
        paymentCallbackInterface?.onSuccess(paymentInfo!!.network, paymentInfo!!.number)
    }

    //cancel transaction
    private fun cancelTransaction() {
        dismiss()
        paymentCallbackInterface?.onReceivedData(paymentInfo!!, getString(R.string.transaction_cancelled))
    }


    // transaction failed
    private fun transactionFailed(message: String) {
        cancelTimerAction()
        buttonOptions.visibility = View.VISIBLE
        buttonCancel.visibility = View.VISIBLE
        paymentProgress.visibility = View.GONE
        textViewMessage.text = message
        textViewMessage.setTextColor(requireActivity().resources!!.getColor(R.color.colorRed))
    }

    fun paymentRequest(paymentInfo: MoMoPaymentInfo) {
        retrofit.paymentRequest(paymentInfo).enqueue(
                object : Callback<MoMoPaymentInfo> {
                    override fun onFailure(call: Call<MoMoPaymentInfo>, t: Throwable) {

                    }

                    override fun onResponse(call: Call<MoMoPaymentInfo>, response: Response<MoMoPaymentInfo>) {
                        val result = response.body()

                        if(response.isSuccessful){
                            Log.e("response","success")
                        }

                    }
                }
        )
    }

    //retry transaction
    private fun retryMomoTransaction(paymentInfo: MoMoPaymentInfo){
        retrofit.retryPayment(paymentInfo).enqueue(
                object : Callback<MoMoPaymentInfo> {
                    override fun onFailure(call: Call<MoMoPaymentInfo>, t: Throwable) {

                        if(paymentProgress!=null && textViewMessage!=null && buttonOptions!=null){
                            paymentProgress.visibility = View.GONE
                            textViewMessage.text = t.toString()
                            buttonOptions.visibility = View.VISIBLE
                        }

                    }

                    override fun onResponse(call: Call<MoMoPaymentInfo>, response: Response<MoMoPaymentInfo>) {
                        val result = response.body()

                        paymentProgress.visibility = View.GONE
                        textViewMessage.text = response.message()
                        buttonOptions.visibility = View.VISIBLE
                        buttonMobileMoneyAction.visibility = View.VISIBLE


                    }
                }
        )
    }

   // timer checking status
    private fun updatePaymentStatus() {
        timer = object : CountDownTimer(time, 10000) {
            override fun onTick(millisUntilFinished: Long) {
                time = 200000L
                checkPaymentStatus()
            }

            override fun onFinish() {
            }
        }
        timer?.start()
    }


    private fun showPaymentIcon() {
        Log.e("network",paymentInfo!!.network)
        textViewGateWay.text = "${paymentInfo?.network}"
        textViewNumber.text = "${paymentInfo?.number}"
        var imageIcon = R.drawable.ic_mtn_money
        when (paymentInfo?.network) {
            PaymentNetworks.VODAFONE.name -> imageIcon = R.drawable.ic_vodafone_cash
            PaymentNetworks.AIRTEL.name -> imageIcon = R.drawable.ic_airtel_money
            PaymentNetworks.TIGO.name -> imageIcon = R.drawable.ic_tigo_cash
        }
        imageViewGetWayIcon.setImageResource(imageIcon)
    }

    private fun cancelTimerAction() {
        this.timer?.cancel()
        //this.paymentInfo?.cancelRequest()
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