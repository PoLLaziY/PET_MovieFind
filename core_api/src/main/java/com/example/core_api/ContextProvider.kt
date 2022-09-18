package com.example.core_api

import android.content.Context

interface ContextProvider {
    fun provideContext(): Context
}