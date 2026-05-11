package com.lhh.mvvmsample.data.remote

import retrofit2.http.GET

interface ApiService {

    /** 与服务端 `/selected` 一致：仅返回桌面应用里勾选过的图片 */
    @GET("selected")
    suspend fun getSelectedImages(): ImagesResponseDto
}
