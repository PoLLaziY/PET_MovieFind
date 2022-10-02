package com.example.core_api

import com.example.domain.WebResourceState
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface WebResourceStateListener {
    val webResourceState: BehaviorSubject<WebResourceState>
}