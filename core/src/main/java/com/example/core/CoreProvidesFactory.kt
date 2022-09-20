package com.example.core

import android.content.Context
import com.example.core_api.CoreComponent
import com.example.core_impl.DaggerCoreComponentImpl

object CoreProvidesFactory {

    fun provideCoreComponent(context: Context): CoreComponent {
        return DaggerCoreComponentImpl.factory().create(context)
    }
}