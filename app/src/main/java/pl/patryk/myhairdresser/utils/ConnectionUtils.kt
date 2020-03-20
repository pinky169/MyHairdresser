package pl.patryk.myhairdresser.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionUtils(context: Context) {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
    val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
}