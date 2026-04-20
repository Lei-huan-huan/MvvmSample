package com.lhh.mvvmsample.data.remote

import com.google.gson.annotations.SerializedName

data class ImageItemDto(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("relativePath")
    val relativePath: String? = null,
    @SerializedName("size")
    val size: Long? = null,
    @SerializedName("lastModified")
    val lastModified: String? = null,
    @SerializedName("url")
    val url: String? = null
)
