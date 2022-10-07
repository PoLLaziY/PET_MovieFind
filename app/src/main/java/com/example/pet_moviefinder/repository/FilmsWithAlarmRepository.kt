package com.example.pet_moviefinder.repository

import android.util.Log
import com.example.core_api.DataService
import com.example.core_api.WebService
import com.example.domain.Film
import com.example.domain.NetworkState
import com.example.domain.WebResourceState
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class FilmsWithAlarmRepository(private val dataService: DataService, private val webService: WebService): FilmsRepository {

    override val allFilmsList: BehaviorSubject<List<Film>> = BehaviorSubject.create()
    override val searchedFilmsList: BehaviorSubject<List<Film>> = BehaviorSubject.createDefault(
        emptyList()
    )

    override val loading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(true)
    override val networkState: Observable<NetworkState> = webService.networkState
    override val webResourceState: Observable<WebResourceState> = webService.webResourceState

    private val _dataFilms = dataService.getFilmsWithAlarm().buffer(1).map {
        Log.i("SSS", "_dataFilms get Films")
        it[0] }
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

    init {
        loadFilms()
    }

    override fun getNextFilmsPage(page: Int): Boolean {
        return false
    }

    override fun getNextSearchedPage(query: String, page: Int): Boolean {
        return false
    }

    override fun loadFilms() {
        loadProcess?.dispose()
        loadProcess = _dataFilms.subscribeOn(Schedulers.io())
            .subscribe {
                Log.i("SSS", "_dataFilms send to allFilms ${it.size}")
                allFilmsList.onNext(it)
                loadProcess = null
            }
    }

    override fun searchFilms(query: String) {
        searchProcess?.dispose()
        searchProcess = _dataFilms.map { list ->
            list.filter {
                it.title.contains(query, true) || it.description.contains(query, true)
            }
        }.subscribeOn(Schedulers.io())
            .subscribe {
                searchedFilmsList.onNext(it)
                searchProcess = null
            }
    }
}