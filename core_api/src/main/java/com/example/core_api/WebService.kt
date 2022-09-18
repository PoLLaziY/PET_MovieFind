package com.example.core_api

import com.example.domain.Film
import com.example.domain.FilmsCategory
import com.example.domain.Language
import io.reactivex.rxjava3.core.Single

interface WebService {
    fun getFilms(page: Int, category: FilmsCategory, language: Language): Single<List<Film>>
    fun searchFilms(query: String, page: Int, language: Language): Single<List<Film>>
}