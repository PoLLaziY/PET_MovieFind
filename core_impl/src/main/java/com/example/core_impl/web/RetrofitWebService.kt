package com.example.core_impl.web

import com.example.core_api.WebService
import com.example.core_impl.web.retrofit.RetrofitAPI
import com.example.domain.*
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

class RetrofitWebService(private val retrofitService: RetrofitAPI) :
    WebService {

    private val webResourceState: BehaviorSubject<WebResourceState> =
        BehaviorSubject.createDefault(WebResourceState.CONNECT)

    override fun getWebResourceState(): Observable<WebResourceState> {
        return webResourceState.distinctUntilChanged()
    }

    override fun getFilms(
        page: Int,
        category: FilmsCategory,
        language: Language
    ): Single<out List<Film>> {
        return retrofitService.getFilmsFromAPI(
            page = page,
            category = category.key,
            language = language.key,
            apiKey = ConstForRestAPI.KEY
        ).doOnError {
            webResourceState.onNext(WebResourceState.DISCONNECT)
        }.map {
            webResourceState.onNext(WebResourceState.CONNECT)
            it.results
        }
    }

    override fun searchFilms(
        query: String,
        page: Int,
        language: Language
    ): Single<out List<Film>> {
        return retrofitService.searchFilmsFromAPI(
            query = query,
            page = page,
            language = language.key,
            apiKey = ConstForRestAPI.KEY
        ).doOnError {
            webResourceState.onNext(WebResourceState.DISCONNECT)
        }.map {
            webResourceState.onNext(WebResourceState.CONNECT)
            it.results
        }
    }

}