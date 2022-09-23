package com.example.pet_moviefinder

import com.example.domain.Film
import com.example.domain.NetworkState
import com.example.pet_moviefinder.repository.NavigationRepository
import io.reactivex.rxjava3.core.Observable

interface Navigation: NavigationRepository {
    fun onNavigationClick(itemId: Int): Boolean
    fun onFilmItemClick(film: Film): Boolean
    fun subscribe(activity: MainActivity)
    fun unSubscribe()
    fun onDownloadImageClick(film: Film)
    fun onShareButtonClick(film: Film)
}