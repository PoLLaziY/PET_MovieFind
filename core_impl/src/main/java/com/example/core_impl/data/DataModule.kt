package com.example.core_impl.data

import android.content.Context
import com.example.core_api.DataService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideDataService(context: Context): DataService = RoomSharedPrefsDataService(context)
}