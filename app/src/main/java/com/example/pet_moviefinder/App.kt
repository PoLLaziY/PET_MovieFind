package com.example.pet_moviefinder

import android.app.Application

class App: Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object {
        lateinit var app: App
    }
}