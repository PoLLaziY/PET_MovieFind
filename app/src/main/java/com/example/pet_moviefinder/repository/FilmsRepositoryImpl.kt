package com.example.pet_moviefinder.repository

import android.util.Log
import com.example.core_api.DataService
import com.example.core_api.WebService
import com.example.domain.Film
import com.example.domain.FilmsCategory
import com.example.domain.Language
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class FilmsRepositoryImpl(
    private val webService: WebService,
    private val dataService: DataService,
    private val settingsRepository: SettingsRepository
) :
    FilmsRepository {

    override val searchedFilmsList: BehaviorSubject<List<Film>> =
        BehaviorSubject.createDefault(emptyList())
    override val allFilmsList: BehaviorSubject<List<Film>> =
        BehaviorSubject.createDefault(emptyList())
    override val loading: BehaviorSubject<Boolean> =
        BehaviorSubject.createDefault(false)

    private var lastSearchResultFromWeb: Boolean = true
    private var isLastSearchedPage: Boolean = false
    private var lastFilmsFromWeb: Boolean = true
    private var isLastFilmPage: Boolean = false

    private val dataProcess: CompositeDisposable = CompositeDisposable()

    private var searchProcess: Disposable? = null
        set(value) {
            field = value
            if (value != null) loading.onNext(true)
            else {
                if (loadProcess == null) loading.onNext(false)
            }
        }
    private var loadProcess: Disposable? = null
        set(value) {
            field = value
            if (value != null) loading.onNext(true)
            else {
                if (searchProcess == null) loading.onNext(false)
            }
        }


    private var category: BehaviorSubject<FilmsCategory> = settingsRepository.filmsCategory

    override fun getNextFilmsPage(page: Int): Boolean {
        val iCan = canGetNextFilmsPage()
        if (!iCan) {
            return false
        }
        loadFilms(page, {
            val list = allFilmsList.value
            allFilmsList.onNext(list?.toMutableList()?.apply { addAll(it) } ?: it)
        }, {

        })
        return true
    }

    override fun loadFilms() {
        loadFilms(1, {
            allFilmsList.onNext(it)
        }, {
            loadProcess = loadFilmsFromData()
                .subscribe({
                    allFilmsList.onNext(it)
                    lastFilmsFromWeb = false
                    loadProcess = null
                }, {
                    loadProcess = null
                })
        })
    }

    override fun getNextSearchedPage(query: String, page: Int): Boolean {
        val iCan = canGetNextSearchedFilmsPage()
        if (!iCan) return false
        searchFilms(query, page,
            onNextFromWeb = {
                val list = searchedFilmsList.value
                searchedFilmsList.onNext(list?.toMutableList()?.apply { addAll(it) } ?: it)
                Log.i("VVV", "Searched list size ${it.size}")
                lastSearchResultFromWeb = true
            }, onErrorFromWeb = {

            })
        return true
    }

    override fun searchFilms(query: String) {
        searchFilms(query, 1,
            onNextFromWeb = {
                searchedFilmsList.onNext(it)
                lastSearchResultFromWeb = true
            }, onErrorFromWeb = {
                searchProcess = loadFilmsFromData().subscribe({
                    searchedFilmsList.onNext(it)
                    lastSearchResultFromWeb = false
                    searchProcess = null
                }, {
                    searchProcess = null
                })
            })
    }

    private fun canGetNextFilmsPage(): Boolean {
        Log.i("VVV", "web is $lastFilmsFromWeb, load is ${loadProcess != null}")
        return lastFilmsFromWeb && loadProcess == null && !isLastFilmPage
    }

    private fun canGetNextSearchedFilmsPage(): Boolean =
        lastSearchResultFromWeb && searchProcess == null && !isLastSearchedPage

    private inline fun searchFilms(
        query: String,
        page: Int,
        crossinline onNextFromWeb: ((list: List<Film>) -> Unit) = {},
        crossinline onErrorFromWeb: ((e: Throwable) -> Unit) = {}
    ) {
        searchProcess?.dispose()
        searchProcess = searchFilmsFromWeb(query, page)
            .subscribe({
                onNextFromWeb(it)
                isLastSearchedPage = it.isEmpty()
                safeToData(it)
                searchProcess = null
            }, {
                onErrorFromWeb(it)
                searchProcess = null
            })
    }

    private inline fun loadFilms(
        page: Int,
        crossinline onNextFromWeb: (list: List<Film>) -> Unit,
        crossinline onErrorFromWeb: (e: Throwable) -> Unit
    ) {
        loadProcess?.dispose()
        loadProcess = getFilmsFromWeb(page)
            .subscribe({
                onNextFromWeb(it)
                safeToData(it)
                isLastFilmPage = it.isEmpty()
                loadProcess = null
            }, {
                onErrorFromWeb(it)
                loadProcess = null
            })

    }

    private fun safeToData(list: List<Film>) {
        dataProcess.add(
            Completable.create {
                dataService.insertFilms(list)
                it.onComplete()
            }.subscribeOn(Schedulers.io())
                .subscribe()
        )

    }

    private fun getFilmsFromWeb(page: Int): Single<List<Film>> =
        webService.getFilms(page, category.value!!, Language.RUS).subscribeOn(Schedulers.io())

    private fun searchFilmsFromWeb(query: String, page: Int): Single<List<Film>> =
        webService.searchFilms(query, page, Language.RUS).subscribeOn(Schedulers.io())

    private fun loadFilmsFromData(): Single<List<Film>> =
        dataService.getAllFilms().singleOrError().subscribeOn(Schedulers.io())
}