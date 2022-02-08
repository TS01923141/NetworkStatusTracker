package com.example.networkstatustracker.model

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

sealed class NetworkStatus {
    object Available : NetworkStatus()
    object Unavailable : NetworkStatus()
}

@SuppressLint("MissingPermission")
class NetworkStatusTracker(context: Context) {
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkStatus = callbackFlow<NetworkStatus> {
        val networkStatusCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkStatus.Available)
            }

            override fun onLost(network: Network) {
                trySend(NetworkStatus.Unavailable)
            }

            override fun onUnavailable() {
                trySend(NetworkStatus.Unavailable)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkStatusCallback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(networkStatusCallback)
//                cancel()
        }
    }
}

//https://elegant-access-kc.medium.com/android-kotlin-%E6%A6%82%E5%BF%B5%E7%AF%87-%E4%BD%A0%E9%9C%80%E8%A6%81%E7%9F%A5%E9%81%93%E7%9A%84-inline-noinline-crossinline-%E4%BD%BF%E7%94%A8%E8%A9%B3%E8%A7%A3-1a2ebf9a049b
//使用 inline 能避免建立過多的實例化物件
//使用 noinline 可以讓lambda 不使用inline
//使用 crossinline 可以避免lambda 中的 return 影響外部程式流程
inline fun <Result> Flow<NetworkStatus>.map(
    crossinline onAvailable: suspend () -> Result,
    crossinline onUnavailable: suspend () -> Result,
): Flow<Result> = map { status ->
    when (status) {
        NetworkStatus.Available -> onAvailable()
        NetworkStatus.Unavailable -> onUnavailable()
    }
}