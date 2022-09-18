package com.example.pet_moviefinder

import android.content.Context
import com.example.core.CoreProvidesFactory
import com.example.core_api.ContextProvider
import com.example.core_api.CoreComponent
import com.example.core_api.DataService
import com.example.core_api.WebService
import com.example.pet_moviefinder.repository.*
import com.example.pet_moviefinder.view_model.SettingsFragmentModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
abstract class AppComponent : ContextProvider {

    val provideNavigation: Navigation by lazy {
        NavigationImpl()
    }

    private val provideCoreComponent: CoreComponent by lazy {
        CoreProvidesFactory.provideCoreComponent(this)
    }

    private val provideWebService: WebService by lazy {
        provideCoreComponent.provideWebService()
    }

    val provideDataService: DataService by lazy {
        provideCoreComponent.provideDataService()
    }

    private val provideSettingsRepository: SettingsRepository by lazy {
        SettingsRepositoryImpl(provideDataService)
    }

    val provideSettingsFragmentModel: SettingsFragmentModel by lazy {
        SettingsFragmentModel(provideSettingsRepository, provideNavigation)
    }

    val provideFilmRepository: FilmsRepository by lazy {
        FilmsRepositoryImpl(provideWebService, provideDataService, provideSettingsRepository)
    }

    val provideFavoriteFilmsRepository: FilmsRepository by lazy {
        FavoriteFilmsRepositoryImpl(provideDataService)
    }

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}