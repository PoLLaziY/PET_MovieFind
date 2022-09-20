package com.example.core_impl

import android.content.Context
import com.example.core_api.CoreComponent
import com.example.core_impl.data.DataModule
import com.example.core_impl.web.WebModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [WebModule::class, DataModule::class])
abstract class CoreComponentImpl: CoreComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): CoreComponentImpl
    }
}