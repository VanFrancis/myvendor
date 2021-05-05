package com.mobilepoc.myvendor.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class Util {
    companion object aquiVaiMetodosEstaticos {

        fun exibirToast(context: Context, mensagem: String){
            Toast.makeText(context, mensagem , Toast.LENGTH_LONG).show()
        }

    }
}