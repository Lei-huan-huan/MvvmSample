package com.lhh.mvvmsample.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("images")
    suspend fun getImages(
        @Query("page") page: Int? = null
    ): ImagesResponseDto
}
