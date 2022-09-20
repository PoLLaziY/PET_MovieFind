package com.example.pet_moviefinder

import android.app.Application
import com.example.pet_moviefinder.di.AppComponent
import com.example.pet_moviefinder.di.DaggerAppComponent

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