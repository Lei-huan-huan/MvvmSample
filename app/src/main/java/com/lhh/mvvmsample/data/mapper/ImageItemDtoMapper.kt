package com.lhh.mvvmsample.data.mapper

import com.lhh.mvvmsample.data.local.ImageEntity
import com.lhh.mvvmsample.data.remote.ImageItemDto

fun ImageItemDto.toImageEntityOrNull(updatedAtMillis: Long): ImageEntity? {
    val safeUrl = url?.trim().orEmpty()
    if (safeUrl.isEmpty()) return null
    val safeName = name?.trim().takeUnless { it.isNullOrEmpty() }
        ?: safeUrl.substringAfterLast('/').ifEmpty { "unknown" }
    return ImageEntity(
        url = safeUrl,
        name = safeName,
        relativePath = relativePath?.trim(),
        size = size ?: 0L,
        lastModified = lastModified?.trim(),
        updatedAtMillis = updatedAtMillis
    )
}
