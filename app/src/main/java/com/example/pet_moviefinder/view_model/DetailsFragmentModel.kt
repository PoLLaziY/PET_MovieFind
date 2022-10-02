package com.example.pet_moviefinder.view_model

import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.*
import com.example.core_api.DataService
import com.example.core_api.WebService
import com.example.domain.Film
import com.example.domain.IconQuality
import com.example.pet_moviefinder.Navigation
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class DetailsFragmentModel(
    private val data: DataService,
    private val webService: WebService,
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
                    ist
                }
                .subscribeOn(Schedulers.io())
                .subscribe {
                    isFavoriteInRepository.onNext(it)
                }
        }

    val isFavoriteInRepository: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(film?.isFavorite?: false)
    private var changingFavoriteState: Disposable? = null

    fun onShareButtonClick() {
        film?.let { navigation.onShareButtonClick(it) }
    }

    fun onDownloadImageClick() {
        if (!navigation.checkPermission()) {
            navigation.requestPermission()
            return
        }

        if (film == null) return
        val film: Film = film!!

        webService.loadFilmIconFromWeb(film, IconQuality.ICON_QUALITY_MEDIUM)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                data.loadToGalleryImage(it, film)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        navigation.makeSnackBarToGallery()
                    }
            }, {
                Log.i("VVV", it.message.toString())
            })

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

    fun setFilmIcon(film: Film?, detailsPoster: AppCompatImageView) {
        webService.loadFilmIconFromWeb(film, IconQuality.ICON_QUALITY_MEDIUM)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                detailsPoster.setImageBitmap(it)
            }, {})
    }
}
