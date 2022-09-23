package com.example.pet_moviefinder

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.domain.ConstForRestAPI
import com.example.domain.Film
import com.example.domain.NetworkState
import com.example.domain.WebResourceState
import com.example.pet_moviefinder.repository.NavigationRepository
import com.example.pet_moviefinder.view.*
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.NullPointerException
import java.net.URL

class NavigationImpl(private val navigationRepository: NavigationRepository) : Navigation {
    private var activity: MainActivity? = null
        set(value) {
            field = value
            if (value != null) {
                if (activeFragment == null) onNavigationClick(R.id.home)
            }
        }

    private val fragmentManager: FragmentManager?
        get() {
            return activity?.supportFragmentManager
        }

    private var activeFragment: Fragment? = null


    private val homeFragment: FilmListFragment by lazy {
        FilmListFragment(
            App.app.appComponent.provideFilmRepository,
            this
        )
    }

    private val favoriteFragment: FilmListFragment by lazy {
        FilmListFragment(
            App.app.appComponent.provideFavoriteFilmsRepository,
            this
        )
    }

    private val settingsFragment: SettingsFragment by lazy {
        SettingsFragment(
            App.app.appComponent.provideSettingsFragmentModel
        )
    }

    private fun setFragment(
        fragment: Fragment?,
        tag: String? = null,
        addToBackstack: Boolean = true
    ) {
        if (fragment == null || fragmentManager == null) return
        val tr = fragmentManager!!.beginTransaction().replace(R.id.fragment_root, fragment, tag)
        if (addToBackstack) tr.addToBackStack(null)
        tr.commit()
        activeFragment = fragment
    }


    override fun onNavigationClick(itemId: Int): Boolean {
        setFragment(
            when (itemId) {
                R.id.home -> homeFragment
                R.id.favorite -> favoriteFragment
                R.id.later -> LaterFragment()
                R.id.selections -> SelectionsFragment()
                R.id.settings -> settingsFragment
                else -> null
            }
        )
        return true
    }

    override fun onDownloadImageClick(film: Film) {
        if (checkPermissions()) {
            loadToGalleryImage(film)
                .subscribeOn(Schedulers.io())
                .subscribe {
                    makeSnackBar()
                }
        } else {
            requestPermission()
            onDownloadImageClick(film)
        }
    }

    override fun onFilmItemClick(film: Film): Boolean {
        if (fragmentManager != null) fragmentManager!!.beginTransaction()
            .add(
                R.id.fragment_root,
                DetailsFragment(film, App.app.appComponent.provideDataService, this)
            ).addToBackStack(null).commit()
        return true
    }

    override fun subscribe(activity: MainActivity) {
        this.activity = activity
    }

    override fun unSubscribe() {
        activity = null
    }

    override fun onShareButtonClick(film: Film) {
        if (activity != null) {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "${film.title} ${film.description}")
            intent.type = "text/plain"
            activity!!.startActivity(Intent.createChooser(intent, "Share To:"))
        }
    }

    override val networkState: Observable<NetworkState> = navigationRepository.networkState
    override val webResourceState: Observable<WebResourceState> = navigationRepository.webResourceState

    private fun checkPermissions(): Boolean {
        if (activity != null) {
            val result = ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            return result == PackageManager.PERMISSION_GRANTED
        }
        return false
    }

    private fun requestPermission() {
        if (activity != null) {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        }
    }

    private fun loadToGalleryImage(film: Film): Completable {
        return Completable.create { completable ->
            if (activity == null) {
                completable.onError(NullPointerException())
                return@create
            }
            val contentResolver = activity!!.contentResolver
            val url = URL("${ConstForRestAPI.IMAGES_URL}original${film.iconUrl}")

            loadBitmap(url).subscribeOn(Schedulers.io())
                .subscribe({
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        val contentValues = ContentValues().apply {
                            put(
                                MediaStore.Images.Media.DISPLAY_NAME,
                                film.title.handleSingleQuote()
                            )
                            put(
                                MediaStore.Images.ImageColumns.DATE_ADDED,
                                System.currentTimeMillis()
                            )
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MovieFinder")
                        }
                        val uri =
                            contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            ) ?: return@subscribe
                        val os = contentResolver.openOutputStream(uri) ?: return@subscribe
                        it.compress(Bitmap.CompressFormat.JPEG, 100, os)
                        os.close()
                    } else {
                        MediaStore.Images.Media.insertImage(
                            contentResolver,
                            it,
                            film.title.handleSingleQuote(),
                            film.description.handleSingleQuote()
                        )
                    }
                    completable.onComplete()
                }, {
                    completable.onError(it)
                })
        }
    }

    private fun loadBitmap(url: URL): Single<Bitmap> {
        return Single.create {
            Glide.with(activity!!).asBitmap().load(url).into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    it.onSuccess(resource)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    it.onError(Exception())
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
        }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }

    private fun makeSnackBar() {
        if (activeFragment != null && activity != null) {
            Snackbar.make(
                activeFragment!!.requireView(),
                Resources.getSystem().getString(R.string.upload),
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.open) {
                    val intent = Intent()
                    intent.action = Intent.ACTION_VIEW
                    intent.type = Resources.getSystem().getString(R.string.image_type_path)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    activity!!.startActivity(intent)
                }
                .show()
        }
    }
}