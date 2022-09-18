package com.example.core_impl.web.retrofit

import com.example.core_impl.web.RestDTO
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//интерфейс работы с базой данных
interface RetrofitAPI {

    //метод получения списка фильмов
    @GET("movie/{category}")
    fun getFilmsFromAPI(
        @Path("category") category: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<RestDTO>

    //метод для поиска на сервере
    @GET("search/movie")
    fun searchFilmsFromAPI(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String,
        @Query("page") page: Int
    ): Single<RestDTO>
}