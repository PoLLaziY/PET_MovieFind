package com.example.core_impl.web

import com.example.core_impl.FilmImpl
import com.google.gson.annotations.SerializedName

data class RestDTO(
    @SerializedName("page") val page : Int,
    @SerializedName("results") val results : List<FilmImpl>,
    @SerializedName("total_results") val total_results : Int,
    @SerializedName("total_pages") val total_pages : Int
)
