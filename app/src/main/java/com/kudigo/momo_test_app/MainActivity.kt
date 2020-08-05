package com.kudigo.momo_test_app

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kudigo.mobile_money_util.MakePayment
import com.kudigo.mobile_money_util.callback.MoMoPaymentCallbackInterface
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val paymentInfo = MoMoPaymentInfo(
            id = "1596648967650",
            network = "MTN",
            number = "0249668268",
            voucherCode = "",
            amount = 200.0
        )

        buttonPay.setOnClickListener {
            MakePayment(this).setApiToken("cdb523ace6569be94a4a8d5e1057f1dcacd006ca").startMoMoPaymentProcessor(paymentInfo,null, object : MoMoPaymentCallbackInterface {


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