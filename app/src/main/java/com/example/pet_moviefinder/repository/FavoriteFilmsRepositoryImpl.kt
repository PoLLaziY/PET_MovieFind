package com.example.pet_moviefinder.repository

import com.example.core_api.DataService
import com.example.domain.Film
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class FavoriteFilmsRepositoryImpl(
    dataService: DataService
) : FilmsRepository {
    override val allFilmsList: BehaviorSubject<List<Film>> = BehaviorSubject.create()
    override val searchedFilmsList: BehaviorSubject<List<Film>> = BehaviorSubject.createDefault(
        emptyList()
    )
    override val loading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(true)

    private val _dataFilms = dataService.getFavoriteFilms().buffer(1).map { it[0] }
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