package com.kudigo.mobile_money_util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kudigo.mobile_money_util.data.JsonArrayResponse
import com.kudigo.mobile_money_util.data.MomoCharge
import com.kudigo.mobile_money_util.retrofit.ApiUrls
import com.kudigo.mobile_money_util.retrofit.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class Utility {

    // check for internet connection
    fun hasNetworkConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
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


}