package com.example.pet_moviefinder.di

import android.content.Context
import com.example.core.CoreProvidesFactory
import com.example.core_api.*
import com.example.pet_moviefinder.Navigation
import com.example.pet_moviefinder.NavigationImpl
import com.example.pet_moviefinder.repository.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoreModule {
    @Provides
    @Singleton
    fun provideWebResStateListener(webService: WebService): WebResourceStateListener = webService

    @Provides
    @Singleton
    fun provideNetworkStateListener(coreComponent: CoreComponent): NetworkStateListener =
        coreComponent.provideNetworkStateListener()

    @Provides
    @Singleton
    fun provideNavigation(): Navigation =
        NavigationImpl()

    @Provides
    @Singleton
    fun provideCoreComponent(context: Context): CoreComponent =
        CoreProvidesFactory.provideCoreComponent(context)

    @Provides
    @Singleton
    fun provideWebService(coreComponent: CoreComponent): WebService =
        coreComponent.provideWebService()

    @Provides
    @Singleton
    fun provideDataService(coreComponent: CoreComponent): DataService =
        coreComponent.provideDataService()

    @Provides
    @Singleton
    fun provideSettingsRepository(dataService: DataService): SettingsRepository =
        SettingsRepositoryImpl(dataService)


    @Provides
    @Singleton
    fun provideFilmRepository(
        webService: WebService,
        dataService: DataService,
        settingsRepository: SettingsRepository
    ): FilmsRepositoryImpl =
        FilmsRepositoryImpl(webService, dataService, settingsRepository)

    @Provides
    @Singleton
    fun provideFavoriteFilmsRepository(dataService: DataService, webService: WebService): FavoriteFilmsRepositoryImpl =
        FavoriteFilmsRepositoryImpl(dataService, webService)

}