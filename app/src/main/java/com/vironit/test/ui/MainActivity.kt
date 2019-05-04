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
import com.vironit.test.data.api.ApiFactory
import com.vironit.test.data.model.UiPage
import com.vironit.test.ui.Location as UserLocation

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    companion object {
        private const val LOCATION_PERMISSION_CODE = 777
        private const val PAGE_START = 1
    }

    private var currentPage = PAGE_START
    private var isLastPage = false
    private var totalPage = ApiFactory.PAGE_LIMIT_SIZE
    private var isLoading = false
    var itemCount = 0

    private lateinit var userLocation: UserLocation

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
        itemCount = 0
        currentPage = PAGE_START
        isLastPage = false
        pagesAdapter.clear()
        mainViewModel.loadPages(userLocation)
    }

    private fun initSwipeContainer() {
        swapContainer = findViewById(R.id.swipe_container)
        swapContainer.setOnRefreshListener(this)
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
                currentPage++
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
                showData(viewState.pages)
            }
            is MainViewState.Error -> {
                showError(viewState.errorMessage)
            }
        }
    }

    private fun showData(pages: List<UiPage>) {
        if (currentPage != PAGE_START) {
            pagesAdapter.removeLoadingItem()
        }
        pagesAdapter.addPages(pages)
        swapContainer.isRefreshing = false
        if (currentPage < totalPage) {
            pagesAdapter.addLoadingItem()
        } else {
            isLastPage = true
        }
        isLoading = false
    }

    private fun showLoading() {
        if (currentPage == PAGE_START) {
            swapContainer.isRefreshing = true
        }
    }

    private fun checkLocationPermission() {
        if ((ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION
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
                //                if (location != null) {
//                    mainViewModel.loadPages(UserLocation(location.latitude, location.longitude))
//                } else {
//                    mainViewModel.loadPages(null)
//                }
                userLocation = UserLocation(53.9, 27.56)
                mainViewModel.loadPages(userLocation)
                return@addOnSuccessListener
            }
    }

    private fun showError(message: String) {
        swapContainer.isRefreshing = false
        Snackbar.make(swapContainer, message, Snackbar.LENGTH_LONG).show()
    }

}