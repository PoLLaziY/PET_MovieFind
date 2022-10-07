package com.example.domain

import android.os.Parcelable

interface Film: Parcelable {
    val id: Int
    val title: String
    val description: String
    val iconUrl: String?
    val rating: Double?
    var isFavorite: Boolean
    var alarmTime: Long?
}