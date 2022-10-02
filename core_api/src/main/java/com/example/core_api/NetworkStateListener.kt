package com.example.core_api

import com.example.domain.NetworkState
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface NetworkStateListener {
    val networkState: BehaviorSubject<NetworkState>
}