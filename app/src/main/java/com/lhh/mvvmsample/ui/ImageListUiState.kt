package com.lhh.mvvmsample.ui

import com.lhh.mvvmsample.data.local.ImageEntity

data class ImageListUiState(
    val images: List<ImageEntity> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val serverIp: String = "",
    val serverPort: String = "8080",
    val toastMessage: String? = null
)
