package com.redbox.shimeji.live.shimejilife.ui.shimejilibrary.pack.data


import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiRepoPacks {
    @GET
    fun getPacks(@Url url: String): Call<PackData>
}