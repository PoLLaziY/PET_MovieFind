package com.example.domain

enum class IconQuality(private val key: String) {
    ICON_QUALITY_LOW("w342"),
    ICON_QUALITY_MEDIUM("w500");

    override fun toString(): String {
        return this.key
    }
}