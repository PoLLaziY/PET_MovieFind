package com.example.pet_moviefinder

import com.example.domain.Film

interface Navigation {
    fun onNavigationClick(itemId: Int): Boolean
    fun onFilmItemClick(film: Film): Boolean
    fun subscribe(activity: MainActivity)
    fun unSubscribe()
    fun onDownloadImageClick(film: Film)
    fun onShareButtonClick(film: Film)
}