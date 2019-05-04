package com.vironit.test.data.repository

import com.vironit.test.data.model.UiPage

interface IPagesNearByRepository {

    suspend fun getPagesNearBy(lat: Double, lon: Double): List<UiPage>

    suspend fun getImagesCountForPage(pageTitle: String): Int

}