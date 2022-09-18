package com.example.core

import com.example.core_api.ContextProvider
import com.example.core_api.CoreComponent
import com.example.core_impl.DaggerCoreComponentImpl

object CoreProvidesFactory {

    fun provideCoreComponent(contextProvider: ContextProvider): CoreComponent {
        return DaggerCoreComponentImpl.builder().contextProvider(contextProvider).build()
    }
}