package com.example.pet_moviefinder

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.core_api.DataService
import com.example.core_api.WebService
import com.example.domain.Film
import com.example.domain.IconQuality
import com.example.pet_moviefinder.utils.*
import com.example.pet_moviefinder.view.*
import com.example.pet_moviefinder.view_model.FilmListFragmentModel
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception
import kotlin.math.abs

class NavigationImpl() : Navigation {

    private var activity: MainActivity? = null

    private val fragmentManager: FragmentManager?
        get() = activity?.supportFragmentManager

    private var activeFragment: Fragment? = null

    private val homeFragment: FilmListFragment by lazy {
        FilmListFragment(
            App.app.appComponent.provideFilmsModel()
        )
    }

    private val favoriteFragment: FilmListFragment by lazy {
        FilmListFragment(
            App.app.appComponent.provideFavoriteFilmsModel() as FilmListFragmentModel
        )
    }

    private val alarmFragment: FilmListFragment by lazy {
        FilmListFragment(
            App.app.appComponent.provideFilmsWithAlarmModel() as FilmListFragmentModel
        )
    }

    private val settingsFragment: SettingsFragment by lazy {
        SettingsFragment(
            App.app.appComponent.provideSettingsModel()
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
                R.id.later -> alarmFragment
                R.id.selections -> SelectionsFragment()
                R.id.settings -> settingsFragment
                else -> null
            }, addToBackstack = true
        )
        return true
    }

    override fun onFilmItemClick(film: Film): Boolean {
        setFragment(DetailsFragment(App.app.appComponent.provideDetails(film)))
        return true
    }

    override fun subscribe(activity: MainActivity) {
        this.activity = activity
    }

    override fun unSubscribe() {
        activity = null
    }

    override fun onShareButtonClick(film: Film) {
        activity?.shareFilmPerActionSend(film)
    }

    override fun makeSnackBarToGallery() {
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

    override fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            activity?: return false,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        Log.i("VVV", "Permission ${result == PackageManager.PERMISSION_GRANTED}")
        return result == PackageManager.PERMISSION_GRANTED
    }

    override fun requestPermission() {
        Log.i("VVV", "Request start")
        ActivityCompat.requestPermissions(
            activity?: return,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )

        Log.i("VVV", "Request end")
    }

    override fun pickAbsoluteTime(): Single<Long> {
        return activity?.pickAbsoluteTime()?: Single.error(Exception("Navigation Activity equals Null"))
    }

    override fun createAlarmToWatchLater(film: Film, absoluteTime: Long) {
        activity?.createAlarmToWatchLater(film, absoluteTime)
    }

    override fun closePosterFragment() {
        if (activeFragment is PosterFragment) fragmentManager?.popBackStack()
    }

    override fun showPosterFragment(url: String) {
        val fragment = PosterFragment(url, App.app.appComponent.provideWebService(), this)
        setFragment(fragment)
    }
}