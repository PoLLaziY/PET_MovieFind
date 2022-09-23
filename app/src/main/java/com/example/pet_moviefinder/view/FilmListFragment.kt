package com.example.pet_moviefinder.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.domain.NetworkState
import com.example.domain.WebResourceState
import com.example.pet_moviefinder.view_model.FilmListFragmentModel
import com.example.pet_moviefinder.Navigation
import com.example.pet_moviefinder.R
import com.example.pet_moviefinder.repository.FilmsRepository
import com.example.pet_moviefinder.databinding.FragmentFilmListBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class FilmListFragment(repository: FilmsRepository, navigation: Navigation) : Fragment() {

    private val viewModel: FilmListFragmentModel = FilmListFragmentModel(repository, navigation)
    private lateinit var binding: FragmentFilmListBinding
    private val disposableHandler: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    private val adapter: FilmListAdapter by lazy {
        FilmListAdapter(filmItemClickListener = { viewModel.onFilmItemClick(it) },
            requestPositionListener = { viewModel.listPositionIs(it) })
    }

    override fun onResume() {
        super.onResume()
        disposableHandler.addAll(
            viewModel.filmList
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    adapter.list = it
                },

            viewModel.isRefreshing
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe() {
                    binding.contentView.swipeRefreshLayout.isRefreshing = it
                },

            viewModel.networkState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.appBar.networkState
                        .setImageResource(
                            if (it == NetworkState.CONNECT) R.drawable.ic_baseline_signal_cellular_4_bar_24
                            else R.drawable.ic_baseline_signal_cellular_0_bar_24
                        )
                },

            viewModel.webResState
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    binding.appBar.webResState
                        .setImageResource(
                            if (it == WebResourceState.CONNECT) R.drawable.ic_baseline_cloud_queue_24
                            else R.drawable.ic_baseline_cloud_off_24
                        )
                }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //настройка RecyclerView
        binding.contentView.rv.adapter = adapter

        //настройка кнопки МЕНЮ верхней навигационной панели
        binding.appBar.appNB.setNavigationOnClickListener {
            viewModel.onNavigationClickListener(it.id)
        }

        //Настройка кнопки НАСТРОЙКИ верхней навигационной панели
        binding.appBar.appNB.setOnMenuItemClickListener {
            viewModel.onNavigationClickListener(it.itemId)
        }

        //настройка нижнего окна навигации
        binding.bottomBar.bottomNV.setOnItemSelectedListener {
            viewModel.onNavigationClickListener(it.itemId)
        }

        //настройка окна поиска
        //при нажатии открывается ввод
        binding.contentView.searchView.setOnClickListener {
            binding.contentView.searchView.isIconified = false
        }

        //при закрытии убрать фокус
        binding.contentView.searchView.setOnCloseListener {
            binding.contentView.searchView.setQuery("", true)
            viewModel.queryFromSearch.onNext("")
            binding.contentView.searchView.clearFocus()
            true
        }

        //добавление слушателя на окно поиска
        binding.contentView.searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.queryFromSearch.onNext(newText ?: "")
                return true
            }
        })

        //настройка свайпа для обновления
        binding.contentView.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshData()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilmListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onPause() {
        super.onPause()
        disposableHandler.clear()
    }
}