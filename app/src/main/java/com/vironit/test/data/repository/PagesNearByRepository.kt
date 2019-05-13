package com.vironit.test.data.repository

import com.vironit.test.data.api.ApiFactory
import com.vironit.test.data.model.ContinueData
import com.vironit.test.data.model.Image
import com.vironit.test.data.model.UiPage
import com.vironit.test.data.model.convertToUiImageList

object PagesNearByRepository : IPagesNearByRepository {

    private var continueString: String? = null

    override suspend fun getPagesNearBy(lat: Double, lon: Double): Pair<List<UiPage>, Boolean> {
        val resultData = mutableListOf<UiPage>()
        val pagesData = ApiFactory.wikiApi.getPagesNearByAsync(
            "$lat|$lon",
            continueString
        ).await()
        continueString = pagesData.continueData?.continueLoadingFrom
        for (page in pagesData.query.pagesList.values) {
            val imagesList = getImagesForPage(page.title)
            resultData.add(UiPage(page.title, imagesList.convertToUiImageList()))
        }
        return Pair(resultData, continueString != null)
    }

    private suspend fun getImagesForPage(pageTitle: String): List<Image> {
        val imagesList = mutableListOf<Image>()
        var continueParam: ContinueData? = null
        do {
            val pagesData = ApiFactory.wikiApi.getImagesDataForPageAsync(
                pageTitle,
                continueParam?.continueLoadingFrom
            ).await()
            continueParam = pagesData.continueData
            val mapValues = pagesData.query.pagesList.values
            for (value in mapValues) {
                imagesList.addAll(value.imagesList ?: listOf())
            }
        } while (continueParam != null)
        return imagesList
    }
}