package com.example.pet_moviefinder.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.domain.FilmsCategory
import com.example.pet_moviefinder.databinding.FragmentSettingsBinding
import com.example.pet_moviefinder.view_model.SettingsFragmentModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class SettingsFragment(private val viewModel: SettingsFragmentModel) : Fragment() {

    lateinit var binding: FragmentSettingsBinding
    private val disposableHandler: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.radioGroup.setOnCheckedChangeListener { _, i ->
            viewModel.onCategoryTypeChanged(i)
        }

        //настройка нижнего окна навигации
        binding.bottomBar.bottomNV.setOnItemSelectedListener { viewModel.onNavigationClickListener(it.itemId) }

    }

    private fun checkTypeRadio(type: FilmsCategory) {
        val button = when (type) {
            FilmsCategory.POPULAR -> binding.popular
            FilmsCategory.TOP -> binding.top
            FilmsCategory.SOON -> binding.soon
            FilmsCategory.IN_CINEMA -> binding.inCinema
            else -> binding.popular
        }
        button.isChecked = true
    }

    override fun onResume() {
        super.onResume()

        disposableHandler.addAll(
            viewModel.categoryType
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    checkTypeRadio(it)
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
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}