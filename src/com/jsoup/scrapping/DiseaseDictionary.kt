package com.jsoup.scrapping

import com.google.gson.annotations.SerializedName

data class DiseaseDictionary(@SerializedName("params") val params: DiseaseDictionaryParams)

data class DiseaseDictionaryParams(
    @SerializedName("id") val id: String
)