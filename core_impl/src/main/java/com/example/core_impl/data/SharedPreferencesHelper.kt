package com.example.core_impl.data

import android.content.Context
import com.example.domain.FilmsCategory

class SharedPreferencesHelper(context: Context) {

    private val pref = context.applicationContext.getSharedPreferences(SettingType.PREFERENCES_NAME.key, Context.MODE_PRIVATE)

    fun getFilmsCategory(): FilmsCategory {
        return when (pref.getString(SettingType.CATEGORY_TYPE.key, FilmsCategory.POPULAR.key)) {
            FilmsCategory.POPULAR.key -> FilmsCategory.POPULAR
            FilmsCategory.IN_CINEMA.key -> FilmsCategory.POPULAR
            FilmsCategory.SOON.key -> FilmsCategory.POPULAR
            FilmsCategory.TOP.key -> FilmsCategory.POPULAR
            else -> FilmsCategory.POPULAR
        }
    }

    fun saveFilmsCategory(category: FilmsCategory): Boolean {
        return pref.edit().putString(SettingType.CATEGORY_TYPE.key, category.key).commit()
    }

    private enum class SettingType(val key: String) {
        PREFERENCES_NAME("settings"),
        CATEGORY_TYPE("category")
    }
}