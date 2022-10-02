package com.example.pet_moviefinder.repository

import com.example.domain.Film
import com.example.domain.NetworkState
import com.example.domain.WebResourceState
import io.reactivex.rxjava3.core.Observable

interface FilmsRepository {
    val allFilmsList: Observable<List<Film>>
    val searchedFilmsList: Observable<List<Film>>
    val loading: Observable<Boolean>
    val networkState: Observable<NetworkState>
    val webResourceState: Observable<WebResourceState>

    fun getNextFilmsPage(page: Int): Boolean
    fun getNextSearchedPage(query: String, page: Int): Boolean
    fun loadFilms()
    fun searchFilms(query: String)
}

