package com.vironit.test.data.model

data class UiPage(val title: String, val images: List<UiImage>)

data class UiImage(val title: String)

fun List<Image>.convertToUiImageList(): List<UiImage> {
    val uiList = mutableListOf<UiImage>()
    forEach {
        uiList.add(UiImage(it.imageTitle))
    }
    return uiList
}