package com.lhh.mvvmsample.data.remote

import com.google.gson.annotations.SerializedName

data class ImagesResponseDto(
    @SerializedName("selectedOnly")
    val selectedOnly: Boolean? = null,
    @SerializedName("count")
    val count: Int? = null,
    @SerializedName("page")
    val page: Int? = null,
    @SerializedName("pageSize")
    val pageSize: Int? = null,
    @SerializedName("totalPages")
    val totalPages: Int? = null,
    @SerializedName("items")
    val items: List<ImageItemDto> = emptyList()
)
