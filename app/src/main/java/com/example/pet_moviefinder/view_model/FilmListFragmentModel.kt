package com.example.pet_moviefinder.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.domain.Film
import com.example.pet_moviefinder.Navigation
import com.example.pet_moviefinder.repository.FilmsRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class FilmListFragmentModel(
    private val repository: FilmsRepository,
    private val navigation: Navigation
) : ViewModel() {


    //Out
    val filmList: BehaviorSubject<List<Film>> = BehaviorSubject.create()
    val isRefreshing: Observable<Boolean> =
        repository.loading.debounce(100, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())

    private var filmsSubscribe: Disposable? = null

    fun onNavigationClickListener(itemId: Int): Boolean {
        return navigation.onNavigationClick(itemId)
    }

    fun onFilmItemClick(film: Film): Boolean {
        return navigation.onFilmItemClick(film)
    }

    //In
    val queryFromSearch: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    //Private
    private var isSearch = false
    private var activePage = 1
    private var activePageSearch = 1

    private fun needUpload(listPosition: Int): Boolean =
        listPosition > (filmList.value?.size ?: 0) - 10

    fun listPositionIs(it: Int) {
        Log.i("VVV", "pos $it, lis size ${filmList.value?.size ?: 0}, needUpload ${needUpload(it)}")
        if (!needUpload(it)) return
        if (!isSearch) {
            if (repository.getNextFilmsPage(activePage + 1)) {
                activePage++
            }
        } else {
            if (repository.getNextSearchedPage(queryFromSearch.value!!, activePageSearch + 1)) {
                activePageSearch++
            }
        }
    }

    fun refreshData() {
        if (!isSearch) {
            refreshAllFilms()
        } else {
            refreshSearchResult(queryFromSearch.value!!)
        }
    }

    init {
        refreshAllFilms()
        queryFromSearch.debounce(200, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .subscribe {
                if (it.isNullOrBlank()) {
                    isSearch = false
                    showAllFilms()
                } else {
                    isSearch = true
                    showSearchFilms()
                    refreshSearchResult(it)
                }
            }
    }

    private fun refreshSearchResult(query: String) {
        activePageSearch = 1
        repository.searchFilms(query)
    }

    private fun refreshAllFilms() {
        activePage = 1
        repository.loadFilms()
    }

    private fun showSearchFilms() {
        filmsSubscribe?.dispose()
        filmsSubscribe = repository.searchedFilmsList.subscribeOn(Schedulers.io())
            .subscribe {
                filmList.onNext(it)
            }
    }

    private fun showAllFilms() {
        filmsSubscribe?.dispose()
        filmsSubscribe = repository.allFilmsList.subscribeOn(Schedulers.io())
            .subscribe {
                filmList.onNext(it)
            }
    }
}