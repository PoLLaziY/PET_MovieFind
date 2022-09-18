package com.example.pet_moviefinder.repository

import com.example.domain.Film
import com.example.domain.FilmsCategory
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface FilmsRepository {
    val allFilmsList: Observable<List<Film>>
    val searchedFilmsList: Observable<List<Film>>
    val loading: Observable<Boolean>

    fun getNextFilmsPage(page: Int): Boolean
    fun getNextSearchedPage(query: String, page: Int): Boolean
    fun loadFilms()
    fun searchFilms(query: String)
}

interface SettingsRepository {
    val filmsCategory: BehaviorSubject<FilmsCategory>
    fun saveFilmsCategory(filmsCategory: FilmsCategory)
}

