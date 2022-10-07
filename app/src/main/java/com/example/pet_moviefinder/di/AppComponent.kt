package com.example.pet_moviefinder.di

import android.content.Context
import com.example.core_api.ContextProvider
import com.example.core_api.DataService
import com.example.core_api.WebService
import com.example.domain.Film
import com.example.pet_moviefinder.Navigation
import com.example.pet_moviefinder.repository.*
import com.example.pet_moviefinder.view_model.*
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [CoreModule::class, ViewModelModule::class])
abstract class AppComponent : ContextProvider {

    abstract fun provideFilmsModel(): FilmListFragmentModel
    abstract fun provideFavoriteFilmsModel(): FavoriteListModel
    abstract fun provideFilmsWithAlarmModel(): FilmsWithAlarmModel
    abstract fun provideSettingsModel(): SettingsFragmentModel
    abstract fun getDetailsFragmentModel(): DetailsFragmentModel
    fun provideDetails(film: Film): DetailsFragmentModel {
        return getDetailsFragmentModel().apply {
            this.film = film
        }
    }
    abstract fun provideNavigation(): Navigation
    abstract fun provideWebService(): WebService
    abstract fun provideDataService(): DataService

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}