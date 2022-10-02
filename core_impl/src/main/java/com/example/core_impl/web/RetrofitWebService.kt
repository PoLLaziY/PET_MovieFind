package com.example.core_impl.web

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.core_api.NetworkStateListener
import com.example.core_api.WebService
import com.example.core_impl.web.retrofit.RetrofitAPI
import com.example.domain.*
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.net.URI
import java.net.URL

class RetrofitWebService(
    private val retrofitService: RetrofitAPI,
    networkStateListener: NetworkStateListener,
    private val requestManager: RequestManager
) :
    WebService {

    override val webResourceState: BehaviorSubject<WebResourceState> =
        BehaviorSubject.createDefault(WebResourceState.CONNECT)

    override val networkState = networkStateListener.networkState

    override fun getFilms(
        page: Int,
        category: FilmsCategory,
        language: Language
    ): Single<out List<Film>> {
        return if (networkState.value == NetworkState.DISCONNECT) Single.error(Exception("NetworkIsDisconnect"))
        else retrofitService.getFilmsFromAPI(
            page = page,
            category = category.key,
            language = language.key,
            apiKey = ConstForRestAPI.KEY
        ).doOnError {
            Log.i("VVV", it.message?:"RetrofitWebServiceError")
            webResourceState.onNext(WebResourceState.DISCONNECT)
        }.map {
            Log.i("VVV", "${it.results}")
            webResourceState.onNext(WebResourceState.CONNECT)
            it.results
        }
    }

    override fun searchFilms(
        query: String,
        page: Int,
        language: Language
    ): Single<out List<Film>> {
        return if (networkState.value == NetworkState.DISCONNECT) Single.error(Exception("NetworkIsDisconnect"))
        else retrofitService.searchFilmsFromAPI(
            query = query,
            page = page,
            language = language.key,
            apiKey = ConstForRestAPI.KEY
        ).doOnError {
            webResourceState.onNext(WebResourceState.DISCONNECT)
        }.map {
            webResourceState.onNext(WebResourceState.CONNECT)
            it.results
        }
    }

    override fun loadFilmIconFromWeb(film: Film?, quality: IconQuality): Single<Bitmap> {
        return Single.create { single ->
            if (film?.iconUrl == null) {
                single.onError(Exception("Glide <- Film == null"))
                return@create
            }
            requestManager
                .asBitmap()
                .load(film.iconURI(quality))
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        single.onSuccess(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        single.onError(Exception("GlideIconLoadException"))
                    }
                })
        }
    }

    override fun ImageView.setFilmIconFromWeb(
        film: Film?,
        iconQuality: IconQuality
    ) {
        if (film?.iconUrl == null) return
        Glide.with(this)
            .load(film.iconURI(iconQuality))
            .centerCrop()
            .into(this)
    }


    private fun Film.iconURI(iconQuality: IconQuality): URI? {
        if (this.iconUrl == null) return null
        return URL(ConstForRestAPI.IMAGES_URL + iconQuality + this.iconUrl).toURI()
    }

}