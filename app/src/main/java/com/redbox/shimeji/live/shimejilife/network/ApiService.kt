package com.redbox.shimeji.live.shimejilife.network

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

// Models are in data.models package
import com.redbox.shimeji.live.shimejilife.data.dataAkimejiT2.PacksResponse

interface ApiService {
    @GET("/packv3.json")
    suspend fun getPacks(): Response<PacksResponse>

    @GET
    suspend fun downloadImage(@Url url: String): Response<ResponseBody>
}

