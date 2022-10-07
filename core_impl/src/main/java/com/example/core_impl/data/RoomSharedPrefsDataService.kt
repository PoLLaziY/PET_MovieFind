package com.example.core_impl.data

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import com.example.core_api.DataService
import com.example.core_impl.data.room.FilmDao
import com.example.core_impl.toFilmImpl
import com.example.domain.Film
import com.example.domain.FilmsCategory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class RoomSharedPrefsDataService(
    private val roomDao: FilmDao,
    private val sharedPrefs: SharedPreferencesHelper,
    private val contentResolver: ContentResolver
) : DataService {

    override fun getAllFilms(): Observable<out List<Film>> {
        return roomDao.getAllFilms()
    }

    override fun getFavoriteFilms(): Observable<out List<Film>> {
        return roomDao.getFavoriteFilms()
    }

    override fun getFilmsWithAlarm(): Observable<out List<Film>> {
        return roomDao.getFilmsWithAlarm()
    }

    override fun insertFilms(films: List<Film>): Completable {
        return roomDao.insertFilms(films.map {
            it.toFilmImpl() })
    }

    override fun deleteFilm(film: Film): Completable {
        return roomDao.deleteFilm(film.toFilmImpl())
    }

    override fun clearAllFilms(): Completable {
        return roomDao.clear()
    }

    override fun clearNotFavoriteFilms(): Completable {
        return roomDao.clearNotMarked()
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

    override fun insertFilm(film: Film): Completable {
        Log.i("SSS", "${film.title} insert to Db")
        return roomDao.insertFilm(film.toFilmImpl())
    }

    override fun loadToGalleryImage(bitmap: Bitmap, film: Film): Completable {
        return Completable.create { completable ->

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                val contentValues = ContentValues().apply {
                    put(
                        MediaStore.Images.Media.DISPLAY_NAME,
                        film.title.handleSingleQuote()
                    )
                    put(
                        MediaStore.Images.ImageColumns.DATE_ADDED,
                        System.currentTimeMillis()
                    )
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MovieFinder")
                }
                val uri =
                    contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    ) ?: return@create
                val os = contentResolver.openOutputStream(uri) ?: return@create
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                os.close()
            } else {
                MediaStore.Images.Media.insertImage(
                    contentResolver,
                    bitmap,
                    film.title.handleSingleQuote(),
                    film.description.handleSingleQuote()
                )
            }
            completable.onComplete()
        }
    }


    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }
}