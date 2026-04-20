package com.lhh.mvvmsample.data.repository

import com.lhh.mvvmsample.data.local.ImageEntity
import kotlinx.coroutines.flow.Flow

interface ImageRepository {
    val images: Flow<List<ImageEntity>>
    suspend fun ensureLoaded()
    suspend fun refresh()
}
