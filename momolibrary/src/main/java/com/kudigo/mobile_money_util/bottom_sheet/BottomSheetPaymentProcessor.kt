package com.kudigo.mobile_money_util.bottom_sheet

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.google.gson.Gson
import com.kudigo.mobile_money_util.MomoChargeType
import com.kudigo.mobile_money_util.MoMoPaymentNetworks
import com.kudigo.mobile_money_util.MoMoPaymentStatus
import com.kudigo.mobile_money_util.R
import com.kudigo.mobile_money_util.callback.MoMoPaymentCallbackInterface
import com.kudigo.mobile_money_util.callback.MomoResultInterface
import com.kudigo.mobile_money_util.data.*
import com.kudigo.mobile_money_util.retrofit.ApiUrls
import com.kudigo.mobile_money_util.retrofit.ServiceBuilder
import kotlinx.android.synthetic.main.bottom_sheet_payment_processor.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal


internal class BottomSheetPaymentProcessor : RoundedBottomSheetDialogFragment() {
    private var paymentCallbackInterface: MoMoPaymentCallbackInterface? = null
    private var paymentInfo: MoMoPaymentInfo? = null
    private var paymentExtraInfo: MoMoPaymentExtraInfo? = null
    private var transactionOrderId:TransactionId? = null
    private var activityCalling: Activity? = null
    private var timer: CountDownTimer? = null
    private var time = 2000000L
    private var momoChargeValue = "*.****"
    private var apiToken = ""
    private val networkOptions = arrayOf(MoMoPaymentNetworks.MTN.name, MoMoPaymentNetworks.VODAFONE.name, MoMoPaymentNetworks.AIRTEL.name, MoMoPaymentNetworks.TIGO.name)
    private val retrofit = ServiceBuilder.buildService(ApiUrls::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.bottom_sheet_payment_processor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //MARK: format the token in a format the api accepts
        apiToken = "Token $apiToken"

       transactionOrderId = TransactionId(
               orderId = paymentInfo!!.id,
               momoTransactionId = paymentInfo!!.id
       )

        buttonMobileMoneyAction.setOnClickListener {
            transactionFinished()
            cancelTimerAction()
        }

        buttonCancel.setOnClickListener {
            cancelTransaction()
        }
        buttonChange.setOnClickListener {
            cancelTimerAction()
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
        val amountRounded = round(paymentInfo!!.amount, 2)
        val charge = getString(R.string.currency) + " $momoChargeValue"
        val orderInformation = "${paymentInfo?.id}" + "\n" + getString(R.string.currency) + "$amountRounded\n$charge"
        textViewOrder.text = orderInformation
        textViewOrderLabel.text = orderLabel
    }


    fun checkPaymentStatus() {
        retrofit.checkPaymentStatus(transactionOrderId!!, apiToken).enqueue(
            object : Callback<TransactionResult> {
                override fun onFailure(call: Call<TransactionResult>, t: Throwable) {
                    transactionFailed(paymentExtraInfo!!.errorMessage)
                }

                override fun onResponse(call: Call<TransactionResult>, response: Response<TransactionResult>) {
                    val result = response.body()?.results

                    Log.e("status",result.toString())
                    if (result?.transactionStatus == MoMoPaymentStatus.SUCCESS.name) {
                        Log.e("status",result.transactionStatus)
                        paymentInfo?.status = MoMoPaymentStatus.SUCCESS.name
                        cancelTimerAction()
                        buttonOptions.visibility = View.GONE
                        paymentProgress.visibility = View.GONE
                        textViewMessage.text = getString(R.string.transaction_successful)
                        textViewMessage.setTextColor(activity!!.resources!!.getColor(R.color.colorPrimary))
                    } else if (result?.transactionStatus == MoMoPaymentStatus.FAILED.name) {
                        paymentInfo?.status = MoMoPaymentStatus.FAILED.name
                        transactionFailed(getString(R.string.transaction_failed))
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
        builder.setSingleChoiceItems(networkOptions, selectedOption) { dialog, which ->
            selectedOption = which
            paymentInfo!!.network = networkOptions[selectedOption]
        }
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
                    if (paymentInfo!!.network == MoMoPaymentNetworks.VODAFONE.name) {
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


    //on done
    private fun transactionFinished() {
        dismiss()
        paymentCallbackInterface?.onSuccess(paymentInfo!!.network, paymentInfo!!.number)
    }

    //cancel transaction
    private fun cancelTransaction() {
        dismiss()
        paymentCallbackInterface?.onCancelled()
    }


    // transaction failed
    private fun transactionFailed(message: String) {
        cancelTimerAction()
        buttonOptions?.visibility = View.VISIBLE
        buttonCancel?.visibility = View.VISIBLE
        paymentProgress?.visibility = View.GONE
        textViewMessage?.text = paymentExtraInfo!!.errorMessage
        textViewMessage.setTextColor(requireActivity().resources!!.getColor(R.color.colorRed))
    }

    //make payment
    private fun paymentRequest(paymentInfo: MoMoPaymentInfo) {
        paymentProgress.visibility = View.VISIBLE
        retrofit.paymentRequest(paymentInfo, apiToken).enqueue(
            object : Callback<MoMoPaymentInfo> {
                override fun onFailure(call: Call<MoMoPaymentInfo>, t: Throwable) {
                    timer?.start()
                    paymentProgress?.visibility = View.GONE
                    textViewMessage?.text = paymentExtraInfo!!.retryMessage
                    buttonOptions?.visibility = View.VISIBLE
                    buttonChange?.visibility = View.VISIBLE
                }

                override fun onResponse(call: Call<MoMoPaymentInfo>, response: Response<MoMoPaymentInfo>) {
                    if (response.isSuccessful) {
                        timer?.start()
                        buttonMobileMoneyAction.visibility = View.VISIBLE
                        textViewMessage.text = paymentExtraInfo!!.authorisationMessage
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
            MoMoPaymentNetworks.VODAFONE.name -> imageIcon = R.drawable.ic_vodafone_cash
            MoMoPaymentNetworks.AIRTEL.name -> imageIcon = R.drawable.ic_airtel_money
            MoMoPaymentNetworks.TIGO.name -> imageIcon = R.drawable.ic_tigo_cash
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
                        momoChargeValue = if (it.chargeType == MomoChargeType.FLAT.name) {
                            it.chargeValue.toString()
                        } else {
                           round(paymentInfo!!.amount.times(it.chargeValue), 2)
                        }
                    }
                    showCharges()
                }
            }
        )
    }


    fun round(value: Double?, numberOfDigitsAfterDecimalPoint: Int): String {
        if (value == null) {
            return value.toString()
        }
        var bigDecimal = BigDecimal(value)
        bigDecimal = bigDecimal.setScale(
            numberOfDigitsAfterDecimalPoint,
            BigDecimal.ROUND_HALF_UP
        )
        return bigDecimal.toPlainString()
    }


    companion object {
        fun newInstance(activity: Activity, apiToken: String, paymentInfo: MoMoPaymentInfo? = null, paymentExtraInfo: MoMoPaymentExtraInfo? = null, callback: MoMoPaymentCallbackInterface) =
            BottomSheetPaymentProcessor().apply {
                this.activityCalling = activity
                this.paymentInfo = paymentInfo
                this.paymentCallbackInterface = callback
                this.apiToken = apiToken
                this.paymentExtraInfo = paymentExtraInfo
            }
    }

    override fun onStop() {
        cancelTimerAction()
        super.onStop()
    }


}