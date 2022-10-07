package com.example.core_api

import android.graphics.Bitmap
import com.example.domain.*
import io.reactivex.rxjava3.core.Single

interface WebService : WebResourceStateListener, NetworkStateListener {
    fun getFilms(page: Int, category: FilmsCategory, language: Language): Single<out List<Film>>
    fun searchFilms(query: String, page: Int, language: Language): Single<out List<Film>>
    fun loadFilmIconFromWeb(film: Film?, quality: IconQuality = IconQuality.ICON_QUALITY_LOW): Single<Bitmap>
    fun loadImageFromWeb(url: String?): Single<Bitmap>
}