package com.kudigo.momo_test_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kudigo.mobile_money_util.MakePayment
import com.kudigo.mobile_money_util.PaymentCallbackInterface
import com.kudigo.mobile_money_util.PaymentInfo
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val amount: Double = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val paymentInfo = PaymentInfo(
            id = "1",
            name = "Jay",
            network = "MTN",
            number = "0244999999",
            reference = "0987",
            status = "",
            voucherCode = "",
            amountPaid = amount
        )

        buttonPay.setOnClickListener {
            MakePayment(this).startMoMoPaymentProcessor(paymentInfo, amount, object : PaymentCallbackInterface {
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

            })
        }
    }
}