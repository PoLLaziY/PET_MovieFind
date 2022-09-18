package com.example.core_impl.web

import com.example.core_api.WebService
import com.example.core_impl.web.retrofit.RetrofitAPI
import com.example.domain.ConstForRestAPI
import com.example.domain.Film
import com.example.domain.FilmsCategory
import com.example.domain.Language
import io.reactivex.rxjava3.core.Single

class RetrofitWebService(private val retrofitService: RetrofitAPI) :
    WebService {

    override fun getFilms(
        page: Int,
        category: FilmsCategory,
        language: Language
    ): Single<List<Film>> {
        return retrofitService.getFilmsFromAPI(
            page = page,
            category = category.key,
            language = language.key,
            apiKey = ConstForRestAPI.KEY
        )
            .map {
            it.results
        }
    }

    override fun searchFilms(
        query: String,
        page: Int,
        language: Language
    ): Single<List<Film>> {
        return retrofitService.searchFilmsFromAPI(
            query = query,
            page = page,
            language = language.key,
            apiKey = ConstForRestAPI.KEY
        )
            .map {
            it.results }
    }

}