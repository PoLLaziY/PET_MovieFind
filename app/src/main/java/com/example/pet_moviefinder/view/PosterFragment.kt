package com.example.pet_moviefinder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.core_api.WebService
import com.example.pet_moviefinder.Navigation
import com.example.pet_moviefinder.databinding.FragmentPosterBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class PosterFragment(private val imageUrl: String, val webService: WebService, private val navigation: Navigation): Fragment() {

    private val binding by lazy {
        FragmentPosterBinding.inflate(LayoutInflater.from(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        webService.loadImageFromWeb(imageUrl).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe ({
                binding.poster
            }, {})

        binding.close.setOnClickListener {
            navigation.closePosterFragment()
        }
        return binding.root
    }
}