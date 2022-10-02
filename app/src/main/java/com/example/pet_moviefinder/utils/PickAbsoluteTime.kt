package com.example.pet_moviefinder.utils

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import io.reactivex.rxjava3.core.Single
import java.util.*

fun Context.pickAbsoluteTime(): Single<Long> {
    return Single.create {

        val calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            { _, y, m, d ->

                TimePickerDialog(
                    this,

                    { _, h, min ->
                        val time = Calendar.getInstance().apply {
                            set(y, m, d, h, min)
                        }.timeInMillis
                        Log.i("VVV", "Calendar Get $time")
                        it.onSuccess(time)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true
                )
                    .show()

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
            .show()
    }

}