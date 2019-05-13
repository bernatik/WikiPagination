package com.vironit.test.data.model

import com.google.gson.annotations.SerializedName

data class PagesData(
    @SerializedName("query") val query: Query,
    @SerializedName("continue") val continueData: ContinueData?
)

data class Query(@SerializedName("pages") val pagesList: Map<String, Page>)

data class ContinueData(@SerializedName("imcontinue") val continueLoadingFrom: String)

data class Page(
    @SerializedName("pageid") val pageId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("images") val imagesList: MutableList<Image>?
)

data class Image(@SerializedName("title") val imageTitle: String)

