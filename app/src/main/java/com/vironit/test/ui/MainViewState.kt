package com.vironit.test.ui

import com.vironit.test.data.model.UiPage

sealed class MainViewState {
    object Loading : MainViewState()
    data class Data(val pages: List<UiPage>) : MainViewState()
    data class Error(val errorMessage: String) : MainViewState()
}