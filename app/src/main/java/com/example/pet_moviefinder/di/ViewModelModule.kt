package com.example.pet_moviefinder.di

import com.example.core_api.DataService
import com.example.core_api.WebService
import com.example.pet_moviefinder.Navigation
import com.example.pet_moviefinder.repository.FavoriteFilmsRepositoryImpl
import com.example.pet_moviefinder.repository.FilmsRepositoryImpl
import com.example.pet_moviefinder.repository.SettingsRepository
import com.example.pet_moviefinder.view_model.DetailsFragmentModel
import com.example.pet_moviefinder.view_model.FavoriteListModel
import com.example.pet_moviefinder.view_model.FilmListFragmentModel
import com.example.pet_moviefinder.view_model.SettingsFragmentModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ViewModelModule {

    @Provides
    @Singleton
    fun provideFilmListFragmentModel(
        repository: FilmsRepositoryImpl,
        navigation: Navigation
    ): FilmListFragmentModel =
        FilmListFragmentModel(repository, navigation)

    @Provides
    @Singleton
    fun provideFavoriteFilmListFragmentModel(
        repository: FavoriteFilmsRepositoryImpl,
        navigation: Navigation
    ): FavoriteListModel =
        FilmListFragmentModel(repository, navigation)

    @Provides
    fun provideDetailsFragmentModel(
        dataService: DataService,
        webService: WebService,
        navigation: Navigation
    ): DetailsFragmentModel =
        DetailsFragmentModel(dataService, webService, navigation)

    @Provides
    @Singleton
    fun provideSettingsFragmentModel(
        repository: SettingsRepository,
        navigation: Navigation
    ): SettingsFragmentModel =
        SettingsFragmentModel(repository, navigation)
}