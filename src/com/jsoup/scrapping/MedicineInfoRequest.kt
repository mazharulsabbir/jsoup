package com.jsoup.scrapping

import com.google.gson.annotations.SerializedName

data class MedicineInfoRequest(
    @SerializedName("params") val params: Params?
)

data class Params(
    @SerializedName("detail_url") val detail_url: String?,
    @SerializedName("image_url") val image_url: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("grey_lighten") val grey_lighten: String?,
    @SerializedName("company") val company: String?,
    @SerializedName("group") val group: String?,
    @SerializedName("price") val price: String?
)