package com.example.core_impl.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.core_impl.FilmImpl

@Database(entities = [FilmImpl::class], exportSchema = false, version = 1)
abstract class FilmRoomDb: RoomDatabase() {
    abstract fun filmDao(): FilmDao
}