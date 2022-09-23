package com.example.core_api

interface CoreComponent {
    fun provideDataService(): DataService
    fun provideWebService(): WebService
    fun provideNetworkStateListener(): NetworkStateListener
}