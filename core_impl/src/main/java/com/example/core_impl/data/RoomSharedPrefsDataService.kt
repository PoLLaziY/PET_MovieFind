package com.example.core_impl.data

import android.content.Context
import androidx.room.Room
import com.example.core_api.DataService
import com.example.core_impl.FilmImpl
import com.example.core_impl.data.room.FilmRoomDb
import com.example.core_impl.toFilmImpl
import com.example.domain.Film
import com.example.domain.FilmsCategory
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class RoomSharedPrefsDataService(context: Context): DataService {

    private val roomDao = Room.databaseBuilder(context, FilmRoomDb::class.java, FilmImpl.Fields.DB_NAME)
        .build().filmDao()

    private val sharedPrefs = SharedPreferencesHelper(context)

    override fun getAllFilms(): Observable<List<Film>> {
        return roomDao.getAllFilms().map {
            it
        }
    }

    override fun getFavoriteFilms(): Observable<List<Film>> {
        return roomDao.getFavoriteFilms().map {
            it
        }
    }

    override fun insertFilms(films: List<Film>) {
        roomDao.insertFilms(films.map { it.toFilmImpl() })
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
        roomDao.insertFilm(film.toFilmImpl())
    }
}