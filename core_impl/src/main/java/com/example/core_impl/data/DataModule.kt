package com.example.core_impl.data

import android.content.ContentResolver
import android.content.Context
import androidx.room.Room
import com.example.core_api.DataService
import com.example.core_impl.FilmImpl
import com.example.core_impl.data.room.FilmDao
import com.example.core_impl.data.room.FilmRoomDb
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideDataService(
        filmDao: FilmDao,
        sharedPreferencesHelper: SharedPreferencesHelper,
        contentResolver: ContentResolver
    ): DataService = RoomSharedPrefsDataService(filmDao, sharedPreferencesHelper, contentResolver)

    @Provides
    @Singleton
    fun provideContentResolver(context: Context): ContentResolver = context.contentResolver

    @Provides
    @Singleton
    fun provideRoomDao(roomDb: FilmRoomDb): FilmDao =
        roomDb.filmDao()

    @Provides
    @Singleton
    fun provideFilmRoomDb(context: Context): FilmRoomDb =
        Room.databaseBuilder(context, FilmRoomDb::class.java, FilmImpl.Fields.DB_NAME)
            .build()

    @Provides
    @Singleton
    fun provideSharedPreferences(context: Context): SharedPreferencesHelper =
        SharedPreferencesHelper(context)

}