package com.vironit.test.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import com.vironit.test.R
import com.vironit.test.data.model.UiPage
import com.vironit.test.ui.Location as UserLocation

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        private const val LOCATION_PERMISSION_CODE = 777
        private const val FIRST_LOADING = 1
    }

    private var loadingCounter = FIRST_LOADING
    private var isLastPage = false
    private var isLoading = false

    private var userLocation: UserLocation? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mainViewModel: MainViewModel
    private lateinit var pagesAdapter: PagesAdapter
    private lateinit var swapContainer: SwipeRefreshLayout
    private lateinit var pagesRv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSwipeContainer()
        initPagesRecycler()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        mainViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
            .create(MainViewModel::class.java)

        mainViewModel.viewState.observe(this, Observer {
            handleState(it)
        })

        checkLocationPermission()

    }

    override fun onRefresh() {
        if (userLocation == null) {
            getLastLocation()
        } else {
            loadingCounter = FIRST_LOADING
            isLoading = true
            isLastPage = false
            pagesAdapter.clear()
            mainViewModel.loadPages(userLocation)
        }
    }

    private fun initSwipeContainer() {
        swapContainer = findViewById(R.id.swipe_container)
        swapContainer.setOnRefreshListener(this)
        swapContainer.isRefreshing = true
    }

    private fun initPagesRecycler() {
        pagesRv = findViewById(R.id.rv_articles)
        pagesAdapter = PagesAdapter()
        val layoutManager = LinearLayoutManager(this)
        pagesRv.layoutManager = layoutManager
        pagesRv.adapter = pagesAdapter

        pagesRv.addOnScrollListener(object : PagesScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                loadingCounter++
                mainViewModel.loadPages(userLocation)
            }

            override fun isLastPage(): Boolean = isLastPage

            override fun isLoading(): Boolean = isLoading
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation()
                } else {
                    showError(getString(R.string.error_location_permission))
                }
            }
        }
    }

    private fun handleState(viewState: MainViewState) {
        when (viewState) {
            MainViewState.Loading -> {
                showLoading()
            }
            is MainViewState.Data -> {
                showData(viewState.pages, viewState.hasContinue)
            }
            is MainViewState.Error -> {
                showError(viewState.errorMessage)
            }
        }
    }

    private fun showData(pages: List<UiPage>, hasContinue: Boolean) {
        if (loadingCounter != FIRST_LOADING) {
            pagesAdapter.removeLoadingItem()
        }
        pagesAdapter.addPages(pages)
        swapContainer.isRefreshing = false
        if (hasContinue) {
            pagesAdapter.addLoadingItem()
        }
        isLastPage = !hasContinue
        isLoading = false
    }

    private fun showLoading() {
        if (loadingCounter == FIRST_LOADING) {
            swapContainer.isRefreshing = true
        }
    }

    private fun checkLocationPermission() {
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATION_PERMISSION_CODE
            )
        } else {
            getLastLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                userLocation = if (location != null) {
                    UserLocation(
                        location.latitude,
                        location.longitude
                    )
                } else {
                    null
                }
                mainViewModel.loadPages(userLocation)
                return@addOnSuccessListener
            }
    }

    private fun showError(message: String) {
        swapContainer.isRefreshing = false
        Snackbar.make(swapContainer, message, Snackbar.LENGTH_LONG).show()
    }

}