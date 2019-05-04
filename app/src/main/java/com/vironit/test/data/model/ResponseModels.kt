package com.vironit.test.data.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class PagesData(
    @SerializedName("query") val query: Query,
    @SerializedName("continue") val continueData: ContinueData?
)

data class Query (@SerializedName("pages") val pagesList: Map<String, Page>)

data class ContinueData(@SerializedName("imcontinue") val continueLoadingFrom: String)

data class Page(
    @SerializedName("pageid") val pageId: Int,
    @SerializedName("title") val title: String,
    @SerializedName("images") val imagesList: MutableList<Image>?
)

data class Image(@SerializedName("title") val imageTitle: String) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(imageTitle)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}

