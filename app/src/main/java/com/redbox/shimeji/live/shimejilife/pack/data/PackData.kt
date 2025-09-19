package com.redbox.shimeji.live.shimejilife.ui.shimejilibrary.pack.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PackData(@SerializedName("packs") val packs: List<Packs>) : Serializable {
    data class Packs(
        @SerializedName("id") val id: Int,
        @SerializedName("title") val title: String,
        @SerializedName("sku") val sku: String,
        @SerializedName("targetaudience") val targetaudience: String,
        @SerializedName("icon") val icon: String,
        @SerializedName("partialnude") val partialnude: String,
        @SerializedName("price") val price: Int,
        @SerializedName("shortdescription") val shortdescription: String,
        @SerializedName("description") val description: String,
        @SerializedName("youtubevideo") val youtubevideo: String,
        @SerializedName("promobanner") val promobanner: String,
        @SerializedName("createdAt") val createdAt: String,
        @SerializedName("updatedAt") val updatedAt: String,
        @SerializedName("shimejigif") val shimejigif: ArrayList<ShimejigifImpl>? = null
    ) : Serializable

    class ShimejigifImpl(
        @SerializedName("id") var id: Int? = null,
        @SerializedName("status") var status: String? = null,
        @SerializedName("shimejiGif") var shimejiGif: String? = null,
        @SerializedName("thumb") var thumb: String? = null,
        @SerializedName("name") var name: String? = null,
        @SerializedName("nick") var nick: String? = null,
        @SerializedName("author") var author: String? = null,
        @SerializedName("category") var category: String? = null
    ) : Serializable

}