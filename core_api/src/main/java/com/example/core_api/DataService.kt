package com.example.core_api

import android.graphics.Bitmap
import com.example.domain.Film
import com.example.domain.FilmsCategory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface DataService {
    fun getAllFilms(): Observable<out List<Film>>

    fun getFavoriteFilms(): Observable<out List<Film>>

    fun getFilmsWithAlarm(): Observable<out List<Film>>

    fun insertFilms(films: List<Film>): Completable

    fun deleteFilm(film: Film): Completable

    fun clearAllFilms(): Completable

    fun clearNotFavoriteFilms(): Completable

    fun getFilmsCategory(): Single<FilmsCategory>

    fun saveFilmsCategory(category: FilmsCategory): Single<FilmsCategory>

    fun insertFilm(film: Film): Completable

    fun loadToGalleryImage(bitmap: Bitmap, film: Film): Completable
}