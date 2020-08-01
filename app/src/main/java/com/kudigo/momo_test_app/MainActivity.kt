package com.kudigo.momo_test_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kudigo.mobile_money_util.MakePayment
import com.kudigo.mobile_money_util.callback.PaymentCallbackInterface
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val paymentInfo = MoMoPaymentInfo(
            id = "20",
            network = "MTN",
            number = "0249668268",
            voucherCode = ""
        )

        buttonPay.setOnClickListener {
            MakePayment(this).startMoMoPaymentProcessor(paymentInfo, null, object : PaymentCallbackInterface {
                override fun onSuccess(network: String, number: String) {
                    TODO("Not yet implemented")
                }

                override fun onFailed(reason: String) {
                    TODO("Not yet implemented")
                }

                override fun onCancelled() {
                    TODO("Not yet implemented")
                }

                override fun onReceivedError(message: String) {
                    TODO("Not yet implemented")
                }

                override fun onReceivedData(moMoPaymentInfo: MoMoPaymentInfo, cancelMessage: String) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}