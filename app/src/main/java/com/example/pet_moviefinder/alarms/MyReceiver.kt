package com.example.pet_moviefinder.alarms

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.example.domain.Film
import com.example.pet_moviefinder.utils.FILM_ALARM_ACTION
import com.example.pet_moviefinder.utils.FILM_BUNDLE_KEY
import com.example.pet_moviefinder.utils.FILM_EXTRA_KEY
import com.example.pet_moviefinder.utils.createNotificationToWatchLaterFilm

const val FILM_NOTIFICATION_CHANNEL_ID = "film_alarm_notification_id"
const val FILM_NOTIFY_TAG = "film_notify_tag"

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, p1: Intent?) {

        Log.i("VVV", "Create MyReceiver  ${p1?.action != null && p1.getBundleExtra(FILM_BUNDLE_KEY) != null && (p1.getBundleExtra(FILM_BUNDLE_KEY)!!.getParcelable(FILM_EXTRA_KEY) ?: null as Film) != null}")
        if (p1?.action != FILM_ALARM_ACTION) return

        val bundle: Bundle = p1.getBundleExtra(FILM_BUNDLE_KEY)?: return
        val film: Film = bundle.getParcelable(FILM_EXTRA_KEY)?: return

        context?.createNotificationToWatchLaterFilm(film)
        Log.i("VVV", "Notify Send")
    }
}