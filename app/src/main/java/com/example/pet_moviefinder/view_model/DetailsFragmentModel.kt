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
    private val data: DataService,
    private val navigation: Navigation
) : ViewModel() {

    var film: Film? = null
        set(value) {
            field = value
            if (film == null) return
            isFavoriteInRepository.onNext(film!!.isFavorite)
            data.getFavoriteFilms().buffer(1)
                .map { list ->
                    val ist = list[0].any { it.id == film!!.id }
                    Log.i("VVV", "${film!!.id} $ist")
                    ist}
                .subscribeOn(Schedulers.io())
                .subscribe {
                    isFavoriteInRepository.onNext(it)
                }
        }

    val isFavoriteInRepository: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)
    var changingFavoriteState: Disposable? = null

    fun onShareButtonClick() {
        film?.let { navigation.onShareButtonClick(it) }
    }

    fun onDownloadImageClick() {
        film?.let { navigation.onDownloadImageClick(it) }
    }

    fun changeFavoriteState() {
        if (changingFavoriteState != null) return

        changingFavoriteState = Completable.create {
            film?.isFavorite = isFavoriteInRepository.value == false
            film?.let { it1 -> data.insertFilm(it1) }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .subscribe {
                changingFavoriteState = null
            }

    }
}
