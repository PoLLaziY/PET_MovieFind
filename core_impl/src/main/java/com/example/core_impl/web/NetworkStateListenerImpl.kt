package com.example.core_impl.web

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.core_api.NetworkStateListener
import com.example.domain.NetworkState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.subjects.BehaviorSubject

class NetworkStateListenerImpl(private val context: Context) : NetworkStateListener {

    val networkState: BehaviorSubject<NetworkState> =
        BehaviorSubject.createDefault(NetworkState.DISCONNECT)
    var isRegister = false

    override fun getNetworkState(): Observable<NetworkState> {
        if (!isRegister) registerNetworkListener()
        return networkState.distinctUntilChanged()
    }

    private fun registerNetworkListener() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerNetworkCallBack(context)
        } else {
            registerReceiver(context)
        }
        isRegister = true
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun registerNetworkCallBack(context: Context) {
        val netService =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        netService.registerDefaultNetworkCallback(object :
            ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                networkState.onNext(NetworkState.CONNECT)
            }

            override fun onLost(network: Network) {
                networkState.onNext(NetworkState.DISCONNECT)
            }

            override fun onUnavailable() {
                networkState.onNext(NetworkState.DISCONNECT)
            }
        })
    }

    private fun registerReceiver(context: Context) {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(NetworkBroadcastReceiver(networkState), filter)
    }
}

class NetworkBroadcastReceiver(private val observer: Observer<NetworkState>) : BroadcastReceiver() {
    override fun onReceive(p0: Context?, intent: Intent?) {
        if (intent?.action != ConnectivityManager.CONNECTIVITY_ACTION) return
        val noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
        observer.onNext(
            if (noConnectivity) NetworkState.DISCONNECT else NetworkState.CONNECT
        )
    }
}