package com.example.core_impl.data

import android.util.Log
import com.example.core_api.DataService
import com.example.core_impl.data.room.FilmDao
import com.example.core_impl.toFilmImpl
import com.example.domain.Film
import com.example.domain.FilmsCategory
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class RoomSharedPrefsDataService(
    private val roomDao: FilmDao,
    private val sharedPrefs: SharedPreferencesHelper
) : DataService {

    override fun getAllFilms(): Observable<List<Film>> {
        return roomDao.getAllFilms().map {
            it
        }
    }

    override fun getFavoriteFilms(): Observable<List<Film>> {
        return roomDao.getFavoriteFilms().map {
            Log.i("SSS", "DbIsChanged")
            it
        }
    }

    override fun insertFilms(films: List<Film>) {
        roomDao.insertFilms(films.map {
            it.toFilmImpl() })
    }

    override fun deleteFilm(film: Film) {
        roomDao.deleteFilm(film.toFilmImpl())
    }

    override fun clearAllFilms() {
        roomDao.clear()
    }

    override fun clearNotFavoriteFilms() {
        roomDao.clearNotFavorite()
    }

    override fun getFilmsCategory(): Single<FilmsCategory> {
        return Single.create {
            it.onSuccess(sharedPrefs.getFilmsCategory())
        }
    }

    override fun saveFilmsCategory(category: FilmsCategory): Single<FilmsCategory> {
        return Single.create {
            if (sharedPrefs.saveFilmsCategory(category)) it.onSuccess(category)
            else it.onError(Throwable("Category not safe to SharedPreferences in DataService"))
        }
    }

    override fun insertFilm(film: Film) {
        Log.i("SSS", "${film.title} insert to Db")
        roomDao.insertFilm(film.toFilmImpl())
    }
}