package com.vironit.test.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vironit.test.data.repository.PagesNearByRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private var _viewState = MutableLiveData<MainViewState>()
    val viewState: LiveData<MainViewState>
        get() = _viewState

    fun loadPages(userLocation: Location?) {
        viewModelScope.launch {
            if (userLocation == null) {
                _viewState.postValue(MainViewState.Error("Location is undefined"))
            } else {
                try {
                    withContext(Dispatchers.IO) {
                        val data = PagesNearByRepository.getPagesNearBy(userLocation.latitude, userLocation.longitude)
                        _viewState.postValue(MainViewState.Data(data))
                    }
                } catch (e: Exception) {
                    _viewState.postValue(MainViewState.Error("Error with networking"))
                }
            }
        }

    }

}