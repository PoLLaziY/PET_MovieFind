package com.example.pet_moviefinder.view_model

import android.util.Log
import androidx.lifecycle.*
import com.example.core_api.DataService
import com.example.domain.Film
import com.example.pet_moviefinder.Navigation
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class DetailsFragmentModel(
    val film: Film,
    private val data: DataService,
    private val navigation: Navigation
) : ViewModel() {

    val isFavoriteInRepository: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(film.isFavorite)
    var changingFavoriteState: Disposable? = null

    init {
        data.getFavoriteFilms().buffer(1)
            .map { list ->
                val ist = list[0].any { it.id == film.id }
               Log.i("VVV", "${film.id} $ist")
            ist}
            .subscribeOn(Schedulers.io())
            .subscribe {
                isFavoriteInRepository.onNext(it)
            }
    }

    fun onShareButtonClick() {
        navigation.onShareButtonClick(film)
    }

    fun onDownloadImageClick() {
        navigation.onDownloadImageClick(film)
    }

    fun changeFavoriteState() {
        if (changingFavoriteState != null) return

        changingFavoriteState = Completable.create {
            film.isFavorite = isFavoriteInRepository.value == false
            data.insertFilm(film)
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .subscribe {
                changingFavoriteState = null
            }

    }
}
