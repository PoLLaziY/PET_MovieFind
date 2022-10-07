package com.example.core_impl.data.room

import androidx.room.*
import com.example.core_impl.FilmImpl
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

@Dao
interface FilmDao {
    @Query("SELECT * FROM ${FilmImpl.Fields.TABLE_NAME}")
    fun getAllFilms(): Observable<List<FilmImpl>>

    @Query("SELECT * FROM ${FilmImpl.Fields.TABLE_NAME} WHERE ${FilmImpl.Fields.IS_FAVORITE} = 1")
    fun getFavoriteFilms(): Observable<List<FilmImpl>>

    @Query("SELECT * FROM ${FilmImpl.Fields.TABLE_NAME} WHERE ${FilmImpl.Fields.ALARM_TIME} > 0")
    fun getFilmsWithAlarm(): Observable<List<FilmImpl>>

    //Конфликтная стратегия - не делать транзакцию, чтобы не затереть флаг isFavorite
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFilms(films: List<FilmImpl>): Completable

    @Query("DELETE FROM ${FilmImpl.Fields.TABLE_NAME}")
    fun clear(): Completable

    @Query("DELETE FROM ${FilmImpl.Fields.TABLE_NAME}" +
            " WHERE ${FilmImpl.Fields.IS_FAVORITE} = 0 AND ${FilmImpl.Fields.ALARM_TIME} = 0")
    fun clearNotMarked(): Completable

    @Delete
    fun deleteFilm(film: FilmImpl): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFilm(film: FilmImpl): Completable
}