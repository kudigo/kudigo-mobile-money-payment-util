package com.kudigo.momo_test_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kudigo.mobile_money_util.MakePayment
import com.kudigo.mobile_money_util.MoMoPaymentNetworks
import com.kudigo.mobile_money_util.callback.MoMoPaymentCallbackInterface
import com.kudigo.mobile_money_util.data.MoMoPaymentExtraInfo
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val paymentInfo = MoMoPaymentInfo(
            id = "1596648967650",
            network = MoMoPaymentNetworks.MTN.name,
            number = "0249668268",
            voucherCode = "",
            amount = 200.0
        )

        val errorInfo = MoMoPaymentExtraInfo(
            errorMessage = "Transaction failed",
            authorisationMessage = "Entreat customer to authorize payment",
            retryMessage = "Transaction failed, retry"
        )

        buttonPay.setOnClickListener {
            MakePayment(this).setApiToken(BuildConfig.API_KEY).startMoMoPaymentProcessor(paymentInfo, errorInfo, object : MoMoPaymentCallbackInterface {

                override fun onSuccess(network: String, number: String) {

                }

                override fun onFailed(reason: String) {


                }

                override fun onCancelled() {

                }

                override fun onReceivedError(errorMessage: String) {

                }
            })
        }
    }
}