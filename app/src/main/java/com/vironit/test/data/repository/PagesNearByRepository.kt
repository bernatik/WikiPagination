package com.vironit.test.data.repository

import com.vironit.test.data.api.ApiFactory
import com.vironit.test.data.model.ContinueData
import com.vironit.test.data.model.UiPage

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
            val imagesCount = getImagesCountForPage(page.title)
            resultData.add(UiPage(page.title, imagesCount))
        }
        return Pair(resultData, continueString != null)
    }

    private suspend fun getImagesCountForPage(pageTitle: String): Int {
        var count = 0
        var continueParam: ContinueData? = null
        do {
            val pagesData = ApiFactory.wikiApi.getImagesDataForPageAsync(
                pageTitle,
                continueParam?.continueLoadingFrom
            ).await()
            continueParam = pagesData.continueData
            val mapValues = pagesData.query.pagesList.values
            for (value in mapValues) {
                count += value.imagesList?.size ?: 0
            }
        } while (continueParam != null)
        return count
    }
}