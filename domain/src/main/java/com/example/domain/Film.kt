package com.example.domain

interface Film {
    val id: Int
    val title: String
    val description: String
    val iconUrl: String?
    val rating: Double?
    var isFavorite: Boolean
}