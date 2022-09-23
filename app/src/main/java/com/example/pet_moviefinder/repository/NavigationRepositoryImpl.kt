package com.example.pet_moviefinder.repository

import com.example.core_api.NetworkStateListener
import com.example.core_api.WebResourceStateListener
import com.example.domain.NetworkState
import com.example.domain.WebResourceState
import io.reactivex.rxjava3.core.Observable

class NavigationRepositoryImpl(
    networkStateListener: NetworkStateListener,
    webResourceStateListener: WebResourceStateListener
): NavigationRepository {
    override val networkState: Observable<NetworkState> = networkStateListener.getNetworkState()
    override val webResourceState: Observable<WebResourceState> = webResourceStateListener.getWebResourceState()
}