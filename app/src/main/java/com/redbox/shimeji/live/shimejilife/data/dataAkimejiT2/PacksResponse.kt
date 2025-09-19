package com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2

import com.google.gson.annotations.SerializedName

data class PacksResponse(
    @SerializedName("packs") val packs: List<Pack>
)

data class Pack(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("promobanner") val promobanner: String?,
    @SerializedName("shimejigif") val shimejigif: List<ShimejiGif>
)

data class ShimejiGif(
    @SerializedName("id") val id: Int,
    @SerializedName("nick") val nick: String?,
    @SerializedName("shimejiGif") val shimejiGif: String?,
    @SerializedName("thumb") val thumb: String?,
    @SerializedName("name") val name: String?
)

