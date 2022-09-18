package com.example.pet_moviefinder.repository

import com.example.core_api.DataService
import com.example.domain.FilmsCategory
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject

class SettingsRepositoryImpl(private val dataService: DataService) : SettingsRepository {

    private var safe: Disposable? = null

    override val filmsCategory: BehaviorSubject<FilmsCategory> by lazy {
        val obj = BehaviorSubject.createDefault(FilmsCategory.POPULAR)
        dataService.getFilmsCategory().subscribeOn(Schedulers.io()).observeOn(Schedulers.io())
            .subscribe({
                obj.onNext(it)
            }, {})
        return@lazy obj
    }


    override fun saveFilmsCategory(filmsCategory: FilmsCategory) {
        safe?.dispose()
        safe = dataService.saveFilmsCategory(filmsCategory).subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe({
                this.filmsCategory.onNext(it)
            }, {})
    }
}