package com.kudigo.mobile_money_util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager

class Utility {

    // check for internet connection
    fun hasNetworkConnection(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo.isConnectedOrConnecting
    }




}