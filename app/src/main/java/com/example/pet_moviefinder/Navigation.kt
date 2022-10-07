package com.example.pet_moviefinder

import com.example.domain.Film
import io.reactivex.rxjava3.core.Single

interface Navigation {
    fun onNavigationClick(itemId: Int): Boolean
    fun onFilmItemClick(film: Film): Boolean
    fun subscribe(activity: MainActivity)
    fun unSubscribe()
    fun onShareButtonClick(film: Film)
    fun makeSnackBarToGallery()
    fun checkPermission(): Boolean
    fun requestPermission()
    fun pickAbsoluteTime(): Single<Long>
    fun createAlarmToWatchLater(film: Film, absoluteTime: Long)
}