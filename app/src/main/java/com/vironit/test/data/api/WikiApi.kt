package com.vironit.test.data.api

import com.vironit.test.data.model.PagesData
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface WikiApi {

    @GET("api.php?generator=geosearch")
    fun getPagesNearByAsync(
        @Query(value = ApiFactory.COORDINATE_QUERY_PARAM) coordinates: String,
        @Query(value = ApiFactory.CONTINUE_QUERY_PARAM) imcontinue: String?
    ): Deferred<PagesData>

    @GET("api.php")
    fun getImagesDataForPageAsync(
        @Query(value = ApiFactory.PAGE_TITLE_QUERY_PARAM) title: String,
        @Query(value = ApiFactory.CONTINUE_QUERY_PARAM) imcontinue: String?
    ): Deferred<PagesData>
}