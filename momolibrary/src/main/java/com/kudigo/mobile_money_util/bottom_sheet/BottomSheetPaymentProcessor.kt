package com.kudigo.mobile_money_util.bottom_sheet

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.afollestad.materialdialogs.MaterialDialog
import com.kudigo.mobile_money_util.*
import com.kudigo.mobile_money_util.callback.MoMoPaymentCallbackInterface
import com.kudigo.mobile_money_util.callback.MomoResultInterface
import com.kudigo.mobile_money_util.data.JsonArrayResponse
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo
import com.kudigo.mobile_money_util.data.MomoCharge
import com.kudigo.mobile_money_util.data.MomoTransactionItem
import com.kudigo.mobile_money_util.retrofit.ApiUrls
import com.kudigo.mobile_money_util.retrofit.ServiceBuilder
import kotlinx.android.synthetic.main.bottom_sheet_payment_processor.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BottomSheetPaymentProcessor : RoundedBottomSheetDialogFragment() {
    private var paymentCallbackInterface: MoMoPaymentCallbackInterface? = null
    private var paymentInfo: MoMoPaymentInfo? = null
    private var paymentInterface: MoMoPaymentCallbackInterface? = null
    private var activityCalling: Activity? = null
    private var timer: CountDownTimer? = null
    private var time = 2000000L
    private var momoChargeValue = "*.****"
    private var apiToken = ""
    private val networkOptions = arrayOf(PaymentNetworks.MTN.name, PaymentNetworks.VODAFONE.name, PaymentNetworks.AIRTEL.name, PaymentNetworks.TIGO.name)
    private val retrofit = ServiceBuilder.buildService(ApiUrls::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_payment_processor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            paymentRequest(paymentInfo!!)
        }

        paymentInfo?.let {
            paymentProgress.visibility = View.VISIBLE

            showCharges()
            calculateCharges()
            showPaymentIcon()
            paymentRequest(it)
        }
    }

    private fun showCharges() {
        val orderLabel = getString(R.string.order_amount_charge)
        val amountRounded = Utility().round(paymentInfo!!.amount, 2)
        val charge = "$momoChargeValue " + getString(R.string.currency)
        val orderInformation = "${paymentInfo?.id}" + "\n" + getString(R.string.currency) + "$amountRounded\n$charge"
        textViewOrder.text = orderInformation
        textViewOrderLabel.text = orderLabel
    }


    fun checkPaymentStatus() {
        retrofit.checkPaymentStatus(paymentInfo!!.id, apiToken).enqueue(
                object : Callback<MomoTransactionItem> {
                    override fun onFailure(call: Call<MomoTransactionItem>, t: Throwable) {
                        transactionFailed(t.toString())
                    }

                    override fun onResponse(call: Call<MomoTransactionItem>, response: Response<MomoTransactionItem>) {
                        val result = response.body()
                        if (result?.transactionStatus == PaymentStatus.SUCCESS.name) {
                            paymentInfo?.status = PaymentStatus.SUCCESS.name
                            buttonOptions.visibility = View.GONE
                            paymentProgress.visibility = View.GONE
                            textViewMessage.text = getString(R.string.transaction_successful)
                            textViewMessage.setTextColor(activity!!.resources!!.getColor(R.color.colorPrimary))
                        } else if (result?.transactionStatus == PaymentStatus.FAILED.name) {
                            paymentInfo?.status = PaymentStatus.FAILED.name
                            transactionFailed(response.message())
                        }
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
        }
        builder.show()
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
                object : MomoResultInterface {
                    override fun onReceivedData(data: String) {
                        paymentInfo!!.number = data
                        if (paymentInfo!!.network == PaymentNetworks.VODAFONE.name) {
                            enterVodafoneVoucher()
                            return
                        }
                        retryMomoTransaction()
                    }
                })
    }


    private fun enterVodafoneVoucher() {
        acceptInputDialog(activityCalling!!.getString(R.string.retry_payment_transaction),
                activityCalling!!.getString(R.string.enter_voucher_code_vodafone_cash),
                activityCalling!!.getString(R.string.voucher_code),
                "",
                object : MomoResultInterface {
                    override fun onReceivedData(data: String) {
                        paymentInfo!!.voucherCode = data
                        retryMomoTransaction()
                    }
                })
    }

    //dialog to accept input
    private fun acceptInputDialog(title: String, message: String, hint: String, defaultInput: String? = "", resultInterface: MomoResultInterface) {
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
        buttonOptions?.visibility = View.VISIBLE
        buttonCancel?.visibility = View.VISIBLE
        paymentProgress?.visibility = View.GONE
        textViewMessage?.text = message
        textViewMessage.setTextColor(requireActivity().resources!!.getColor(R.color.colorRed))
    }

    //make payment
    private fun paymentRequest(paymentInfo: MoMoPaymentInfo) {
        paymentProgress.visibility = View.VISIBLE
        retrofit.paymentRequest(paymentInfo, apiToken).enqueue(
                object : Callback<MoMoPaymentInfo> {
                    override fun onFailure(call: Call<MoMoPaymentInfo>, t: Throwable) {
                        paymentProgress?.visibility = View.GONE
                        textViewMessage?.text = t.toString()
                        buttonOptions?.visibility = View.VISIBLE
                        buttonChange?.visibility = View.VISIBLE
                    }

                    override fun onResponse(call: Call<MoMoPaymentInfo>, response: Response<MoMoPaymentInfo>) {
                        val result = response.body()
                        if (response.isSuccessful) {
                            Log.e("response", "success")
                            paymentProgress?.visibility = View.GONE
                            textViewMessage?.text = response.message()
                            buttonMobileMoneyAction?.visibility = View.VISIBLE
                            updatePaymentStatus()
                        }
                    }
                })
    }

    //retry transaction
    private fun retryMomoTransaction() {
        buttonOptions.visibility = View.GONE
        paymentProgress.visibility = View.GONE
        buttonCancel.visibility = View.INVISIBLE
        textViewMessage.visibility = View.VISIBLE

        paymentRequest(paymentInfo!!)
    }


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
        textViewGateWay?.text = paymentInfo?.network
        textViewNumber?.text = paymentInfo?.number
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
    }


    //get charge for momo transaction

    private fun calculateCharges() {
        retrofit.getMomoCharges(apiToken).enqueue(
                object : Callback<JsonArrayResponse> {
                    override fun onFailure(call: Call<JsonArrayResponse>, t: Throwable) {
                        Log.e("error", t.toString())
                    }

                    override fun onResponse(call: Call<JsonArrayResponse>, response: Response<JsonArrayResponse>) {
                        val result = response.body()?.results
                        val chargeResult = result?.find { it.lowerBound <= paymentInfo!!.amount && it.upperBound >= paymentInfo!!.amount }
                        chargeResult?.let {

                            if (it.chargeType == MomoChargeType.FLAT.name) {
                                momoChargeValue = it.chargeValue.toString()
                            } else {
                                getString(R.string.currency) + Utility().round(paymentInfo!!.amount.times(it.chargeValue), 2)
                            }
                        }
                        showCharges()

                    }
                }
        )

    }


    companion object {
        fun newInstance(activity: Activity, apiToken: String, paymentInfo: MoMoPaymentInfo? = null, callback: MoMoPaymentCallbackInterface) =
                BottomSheetPaymentProcessor().apply {
                    this.activityCalling = activity
                    this.paymentInfo = paymentInfo
                    this.paymentInterface = callback
                    this.apiToken = apiToken
                }
    }
}