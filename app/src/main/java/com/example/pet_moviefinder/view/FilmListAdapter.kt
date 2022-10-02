package com.example.pet_moviefinder.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.core_api.WebService
import com.example.domain.Film
import com.example.pet_moviefinder.databinding.FilmItemBinding
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class FilmListAdapter(
    private val filmItemClickListener: ((film: Film) -> Unit)?,
    private val requestPositionListener: ((recyclerPosition: Int) -> Unit)? = null,
    private val webService: WebService
) :
    RecyclerView.Adapter<FilmListAdapter.FilmViewHolder>() {

    var list: List<Film>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        return FilmViewHolder(
            FilmItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    //привязка данных из базы к элементам RecyclerView
    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        //привязка данных из базы к View
        holder.film = list?.get(position)
        requestPositionListener?.invoke(position)
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }


    inner class FilmViewHolder(private val binding: FilmItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        var film: Film? = null
            set(value) {
                if (value != null) {
                    field = value
                    bind(value)
                    binding.root.setOnClickListener {
                        if (film != null) {
                            filmItemClickListener?.invoke(film!!)
                        }
                    }
                }
            }

        private fun bind(film: Film) {
            binding.title.text = film.title
            binding.description.text = film.description
            webService.loadFilmIconFromWeb(film)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    binding.actionImage.setImageBitmap(it)
                }, {})
            binding.rating.rating = (film.rating?.toFloat() ?: 0f) * 10
        }
    }
}
