package com.example.pet_moviefinder.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.domain.ConstForRestAPI
import com.example.domain.Film
import com.example.pet_moviefinder.databinding.FilmItemBinding

class FilmListAdapter(
    private val filmItemClickListener: ((film: Film) -> Unit)?,
    private val requestPositionListener: ((recyclerPosition: Int) -> Unit)? = null
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
            Glide.with(binding.root.context)
                .load(ConstForRestAPI.IMAGES_URL + "w342" + film.iconUrl)
                .centerCrop()
                .into(binding.actionImage)
            binding.rating.rating = (film.rating?.toFloat() ?: 0f) * 10
        }
    }
}
