package com.example.pet_moviefinder.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.domain.Film
import com.example.pet_moviefinder.Navigation
import com.example.pet_moviefinder.repository.FavoriteFilmsRepositoryImpl
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

    //In
    val queryFromSearch: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    //Out
    val filmList: BehaviorSubject<List<Film>> = BehaviorSubject.create()
    val isRefreshing: Observable<Boolean> = repository.loading.subscribeOn(Schedulers.io())

    private var outFilmListSubscribe: Disposable? = null
    //Private
    private var isSearch = false
    private var activePage = 1
    private var activePageSearch = 1

    private fun needUpload(listPosition: Int): Boolean =
        listPosition > (filmList.value?.size ?: 0) - 10

    fun listPositionIs(it: Int) {
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
        outFilmListSubscribe?.dispose()
        outFilmListSubscribe = repository.searchedFilmsList.subscribeOn(Schedulers.io())
            .subscribe {
                filmList.onNext(it)
            }
    }

    private fun showAllFilms() {
        outFilmListSubscribe?.dispose()
        outFilmListSubscribe = repository.allFilmsList.subscribeOn(Schedulers.io())
            .subscribe {
                if (repository is FavoriteFilmsRepositoryImpl) Log.i("SSS", "All Films send ${it.size} to FilmList")
                filmList.onNext(it)
            }
    }


    fun onNavigationClickListener(itemId: Int): Boolean {
        return navigation.onNavigationClick(itemId)
    }

    fun onFilmItemClick(film: Film): Boolean {
        return navigation.onFilmItemClick(film)
    }
}