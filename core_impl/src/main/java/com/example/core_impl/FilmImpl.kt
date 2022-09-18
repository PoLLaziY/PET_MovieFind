package com.example.core_impl

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.Film
import com.google.gson.annotations.SerializedName

@Entity(tableName = FilmImpl.Fields.TABLE_NAME)
class FilmImpl(
    @PrimaryKey(autoGenerate = false) @SerializedName(Fields.ID) override val id: Int,
    @ColumnInfo(name = Fields.TITLE) @SerializedName(Fields.TITLE) override val title: String,
    @ColumnInfo(name = Fields.DESCRIPTION) @SerializedName(Fields.DESCRIPTION) override val description: String,
    @ColumnInfo(name = Fields.ICON_URL) @SerializedName(Fields.ICON_URL) override val iconUrl: String?,
    @ColumnInfo(name = Fields.RATING) @SerializedName(Fields.RATING) override val rating: Double?,
    @ColumnInfo(name = Fields.IS_FAVORITE) override var isFavorite: Boolean = false
) : Film {
    object Fields {
        const val DB_NAME = "film_db"
        const val TABLE_NAME = "films_table"
        const val ICON_URL = "poster_path"
        const val ID = "id"
        const val TITLE = "title"
        const val DESCRIPTION = "overview"
        const val RATING = "vote_average"
        const val IS_FAVORITE = "is_favorite"
    }

    override fun toString(): String {
        return "${if (isFavorite) "Favorite " else ""}Film ${this.title}, ${this.rating}, ${this.id}, ${this.iconUrl}"
    }
}

fun Film.toFilmImpl(): FilmImpl {
    return if (this is FilmImpl) this
    else FilmImpl(
        this.id,
        this.title,
        this.description,
        this.iconUrl,
        this.rating,
        this.isFavorite
    )
}