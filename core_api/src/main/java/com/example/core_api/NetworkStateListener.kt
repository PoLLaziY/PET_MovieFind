package com.example.core_api

import com.example.domain.NetworkState
import io.reactivex.rxjava3.core.Observable

interface NetworkStateListener {
    fun getNetworkState(): Observable<NetworkState>
}