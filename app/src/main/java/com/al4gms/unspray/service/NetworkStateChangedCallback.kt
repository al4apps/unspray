package com.al4gms.unspray.service

import android.net.ConnectivityManager
import android.net.Network

abstract class NetworkStateChangedCallback : ConnectivityManager.NetworkCallback() {

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        onNetworkStateChanged(true)
    }

    override fun onLost(network: Network) {
        super.onLost(network)
        onNetworkStateChanged(false)
    }

    abstract fun onNetworkStateChanged(isAvailable: Boolean)
}
