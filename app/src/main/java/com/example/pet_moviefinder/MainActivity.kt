package com.example.pet_moviefinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.domain.ConstForRestAPI
import com.example.domain.Film
import com.example.pet_moviefinder.databinding.ActivityMainBinding
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable

var isFirst = true

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nav = App.app.appComponent.provideNavigation()
        nav.subscribe(this)

        val film = intent.getParcelableExtra("film") as Film?

        if (film != null) {
            nav.onFilmItemClick(film)
        } else {
            nav.onNavigationClick(R.id.home)

            if (isFirst) {
                FirebaseRemoteConfig.getInstance().setConfigSettingsAsync(FirebaseRemoteConfigSettings.Builder().build())
                FirebaseRemoteConfig.getInstance().fetch(0)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            FirebaseRemoteConfig.getInstance().activate()
                            Completable.create {
                                val posterUrl = FirebaseRemoteConfig.getInstance().getString(ConstForRestAPI.FIREBASE_POSTER_URL_KEY)
                                if (posterUrl.isNotEmpty()) {
                                    nav.showPosterFragment(posterUrl)
                                }
                            }.subscribeOn(AndroidSchedulers.mainThread()).subscribe()
                        }
                    }
            }
        }

        isFirst = false
    }
}
