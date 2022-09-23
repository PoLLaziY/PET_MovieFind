package com.example.core_api

import com.example.domain.WebResourceState
import io.reactivex.rxjava3.core.Observable

interface WebResourceStateListener {
    fun getWebResourceState(): Observable<WebResourceState>
}