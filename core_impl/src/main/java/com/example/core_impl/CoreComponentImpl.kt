package com.example.core_impl

import com.example.core_api.ContextProvider
import com.example.core_api.CoreComponent
import com.example.core_impl.data.DataModule
import com.example.core_impl.web.WebModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(dependencies = [ContextProvider::class], modules = [WebModule::class, DataModule::class])
abstract class CoreComponentImpl: CoreComponent