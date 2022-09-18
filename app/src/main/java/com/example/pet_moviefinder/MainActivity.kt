package com.example.pet_moviefinder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.pet_moviefinder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nav = App.app.appComponent.provideNavigation
        nav.subscribe(this)
    }
}
