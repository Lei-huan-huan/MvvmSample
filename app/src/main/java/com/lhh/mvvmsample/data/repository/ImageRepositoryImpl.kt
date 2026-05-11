package com.lhh.mvvmsample.data.repository

import com.lhh.mvvmsample.data.local.ImageDao
import com.lhh.mvvmsample.data.local.ImageEntity
import com.lhh.mvvmsample.data.mapper.toImageEntityOrNull
import com.lhh.mvvmsample.data.remote.ApiService
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ImageRepositoryImpl @Inject constructor(
    private val imageDao: ImageDao,
    private val apiService: ApiService
) : ImageRepository {

    override val images: Flow<List<ImageEntity>> = imageDao.observeAll()

    override suspend fun ensureLoaded() {
        if (imageDao.getCount() == 0) {
            refresh()
        }
    }

    override suspend fun refresh() {
        withContext(Dispatchers.IO) {
            val response = apiService.getSelectedImages()
            val now = System.currentTimeMillis()
            val entities = response.items
                .distinctBy { it.url }
                .mapNotNull { it.toImageEntityOrNull(now) }

            imageDao.replaceAll(entities)
        }
    }
}
