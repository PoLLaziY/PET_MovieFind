package com.example.pet_moviefinder.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pet_moviefinder.view_model.DetailsFragmentModel
import com.example.pet_moviefinder.R
import com.example.pet_moviefinder.utils.pickAbsoluteTime
import com.example.pet_moviefinder.databinding.FragmentDetailsBinding
import com.example.pet_moviefinder.utils.createAlarmToWatchLater
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class DetailsFragment(private val viewModel: DetailsFragmentModel) : Fragment() {

    private lateinit var binding: FragmentDetailsBinding

    private val disposableHandler: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.detailsTitle.text = viewModel.film?.title
        binding.detailsDescription.text = viewModel.film?.description
        viewModel.setFilmIcon(viewModel.film, binding.detailsPoster)
        if (viewModel.film?.isFavorite == true) binding.favoriteFab.setImageResource(R.drawable.ic_baseline_favorite_24)
        else binding.favoriteFab.setImageResource(R.drawable.ic_baseline_favorite_border_24)

        //слушатели для кнопок
        binding.favoriteFab.setOnClickListener {
            viewModel.changeFavoriteState()
        }

        binding.shareFab.setOnClickListener {
            viewModel.onShareButtonClick()
        }

        binding.download.setOnClickListener {
            viewModel.onDownloadImageClick()
        }

        binding.alarmFab.setOnClickListener {
            viewModel.onAlarmButtonClick()
        }
    }

    override fun onResume() {
        super.onResume()

        disposableHandler.addAll(
            viewModel.isFavoriteInRepository.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Log.i("VVV", "$it In Subscribe")
                    if (it) binding.favoriteFab.setImageResource(R.drawable.ic_baseline_favorite_24)
                    else binding.favoriteFab.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                }
        )
    }

    override fun onPause() {
        disposableHandler.clear()
        super.onPause()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
}