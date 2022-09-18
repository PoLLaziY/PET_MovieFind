package com.example.pet_moviefinder.view_model

import androidx.lifecycle.ViewModel
import com.example.domain.FilmsCategory
import com.example.pet_moviefinder.Navigation
import com.example.pet_moviefinder.R
import com.example.pet_moviefinder.repository.SettingsRepository
import io.reactivex.rxjava3.core.Observable

class SettingsFragmentModel(
    private val repository: SettingsRepository,
    private val navigation: Navigation
) : ViewModel() {

    fun onNavigationClickListener(itemId: Int): Boolean =
        navigation.onNavigationClick(itemId)

    val categoryType: Observable<FilmsCategory> = repository.filmsCategory

    fun onCategoryTypeChanged(buttonId: Int) {
        repository.saveFilmsCategory(
            when (buttonId) {
                R.id.popular -> FilmsCategory.POPULAR
                R.id.top -> FilmsCategory.TOP
                R.id.soon -> FilmsCategory.SOON
                R.id.inCinema -> FilmsCategory.IN_CINEMA
                else -> FilmsCategory.POPULAR
            }
        )
    }
}