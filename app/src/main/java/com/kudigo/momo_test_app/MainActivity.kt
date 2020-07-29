package com.kudigo.momo_test_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kudigo.mobile_money_util.MakePayment
import com.kudigo.mobile_money_util.callback.PaymentCallbackInterface
import com.kudigo.mobile_money_util.data.MoMoPaymentInfo
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
<<<<<<< HEAD
    val amount: Double = 0.0
    val error:String = "You do not have internet connection"
    val paymentInfo = PaymentInfo(
=======


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val paymentInfo = MoMoPaymentInfo(
>>>>>>> dd13ab506c6f693064c1bed483e916ac0dfe6bd3
            id = "1",
            name = "Jay",
            network = "MTN",
            number = "0244999999",
            reference = "0987",
            status = "",
            voucherCode = "",
            amountPaid = 10.0
        )

        buttonPay.setOnClickListener {
<<<<<<< HEAD
            MakePayment(this).showMoMoPaymentProcessor(paymentInfo, amount,error, object : PaymentCallbackInterface {
=======
            MakePayment(this).startMoMoPaymentProcessor(paymentInfo, null, object : PaymentCallbackInterface {
>>>>>>> dd13ab506c6f693064c1bed483e916ac0dfe6bd3
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