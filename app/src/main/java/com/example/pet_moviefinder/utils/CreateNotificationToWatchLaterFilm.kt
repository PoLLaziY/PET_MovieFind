package com.example.pet_moviefinder.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.core_api.WebService
import com.example.domain.Film
import com.example.domain.IconQuality
import com.example.pet_moviefinder.App
import com.example.pet_moviefinder.MainActivity
import com.example.pet_moviefinder.R
import com.example.pet_moviefinder.alarms.FILM_NOTIFICATION_CHANNEL_ID
import com.example.pet_moviefinder.alarms.FILM_NOTIFY_TAG
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

fun Context.createNotificationToWatchLaterFilm(film: Film) {
    val notificationManager =
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val webService = App.app.appComponent.provideWebService()
    val dataService = App.app.appComponent.provideDataService()

    dataService.deleteFilm(film)
        .subscribeOn(Schedulers.io())
        .subscribe {
            Log.i("VVV", "Delete $film")
        }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            FILM_NOTIFICATION_CHANNEL_ID,
            "AlarmToWatch",
            NotificationManager.IMPORTANCE_LOW
        )

        notificationManager.createNotificationChannel(notificationChannel)

        webService
            .loadFilmIconFromWeb(film, IconQuality.ICON_QUALITY_LOW)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val notification = this.buildNotification(film, it, FILM_NOTIFICATION_CHANNEL_ID)
                notificationManager.notify(FILM_NOTIFY_TAG, film.id, notification)
            }, {
                val notification = this.buildNotification(film, null, FILM_NOTIFICATION_CHANNEL_ID)
                notificationManager.notify(FILM_NOTIFY_TAG, film.id, notification)
            })


    } else {
        webService
            .loadFilmIconFromWeb(film, IconQuality.ICON_QUALITY_LOW)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val notification = this.buildNotification(film,it)
                notificationManager.notify(FILM_NOTIFY_TAG, film.id, notification)
            }, {
                val notification = this.buildNotification(film,null)
                notificationManager.notify(FILM_NOTIFY_TAG, film.id, notification)}
            )

    }
}

fun Context.buildNotification(film: Film, bitmap: Bitmap?, channelId: String? = null): Notification {
    val intent = Intent(this, MainActivity::class.java)
        .apply {
            putExtra("film", film)
        }

    val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    return if (channelId != null) NotificationCompat.Builder(this, channelId)
        .setContentTitle("Вы хотели посмотреть сегодня:")
        .setContentInfo(film.title)
        .setSmallIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher))
        .setLargeIcon(bitmap?: BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
        .setContentIntent(pendingIntent)
        .build()
    else NotificationCompat.Builder(this)
        .setContentTitle("Вы хотели посмотреть сегодня:")
        .setContentInfo(film.title)
        .setSmallIcon(IconCompat.createWithResource(this, R.mipmap.ic_launcher))
        .setLargeIcon(bitmap?: BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
        .setContentIntent(pendingIntent)
        .build()
}