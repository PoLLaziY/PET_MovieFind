package com.example.pet_moviefinder.repository

import com.example.domain.FilmsCategory
import io.reactivex.rxjava3.subjects.BehaviorSubject

interface SettingsRepository {
    val filmsCategory: BehaviorSubject<FilmsCategory>
    fun saveFilmsCategory(filmsCategory: FilmsCategory)
}