package com.example.pet_moviefinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.domain.Film
import com.example.pet_moviefinder.databinding.ActivityMainBinding

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
        }
    }
}
