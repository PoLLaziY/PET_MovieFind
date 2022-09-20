package com.example.pet_moviefinder.di

import android.content.Context
import com.example.core_api.ContextProvider
import com.example.core_api.DataService
import com.example.pet_moviefinder.Navigation
import com.example.pet_moviefinder.repository.*
import com.example.pet_moviefinder.view_model.SettingsFragmentModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class])
abstract class AppComponent : ContextProvider {

    abstract val provideNavigation: Navigation
    abstract val provideFilmRepository: FilmsRepositoryImpl
    abstract val provideFavoriteFilmsRepository: FavoriteFilmsRepositoryImpl
    abstract val provideSettingsFragmentModel: SettingsFragmentModel
    abstract val provideDataService: DataService

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}