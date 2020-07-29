package com.kudigo.momo_test_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kudigo.mobile_money_util.MakePayment
import com.kudigo.mobile_money_util.PaymentCallbackInterface
import com.kudigo.mobile_money_util.PaymentInfo
import com.kudigo.mobile_money_util.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val amount: Double = 0.0
    val error:String = "You do not have internet connection"
    val paymentInfo = PaymentInfo(
            id = "1",
            name = "Jay",
            network = "MTN",
            number = "0244999999",
            reference = "0987",
            status = "",
            voucherCode = "",
            amountPaid = 0.0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonPay.setOnClickListener {
            MakePayment(this).showMoMoPaymentProcessor(paymentInfo, amount,error, object : PaymentCallbackInterface {
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