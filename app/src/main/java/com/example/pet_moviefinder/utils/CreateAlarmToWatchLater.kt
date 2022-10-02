package com.example.pet_moviefinder.utils

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.domain.Film
import com.example.pet_moviefinder.alarms.MyReceiver

const val FILM_ALARM_ACTION = "film_alarm_action"
const val FILM_EXTRA_KEY = "film_extra_key"
const val FILM_BUNDLE_KEY = "film_bundle_key"

fun Context.createAlarmToWatchLater(film: Film, absoluteTime: Long) {

    val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(this, MyReceiver::class.java).apply {
        action = FILM_ALARM_ACTION
        val bundle = Bundle().apply {
            this.putParcelable(FILM_EXTRA_KEY, film)
        }
        this.putExtra(FILM_BUNDLE_KEY, bundle)
    }

    val pendingIntent = PendingIntent.getBroadcast(this, film.id, intent, PendingIntent.FLAG_UPDATE_CURRENT)

    alarmManager.set(AlarmManager.RTC, absoluteTime, pendingIntent)

    Log.i("VVV", "Create Alarm ${alarmManager != null && intent != null && pendingIntent != null}")
}

fun Context.shareFilmPerActionSend(film: Film) {
    val intent = Intent()
    intent.action = Intent.ACTION_SEND
    intent.putExtra(Intent.EXTRA_TEXT, "${film.title} ${film.description}")
    intent.type = "text/plain"
    this.startActivity(Intent.createChooser(intent, "Share To:"))
}