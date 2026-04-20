package com.lhh.mvvmsample.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey val url: String,
    val name: String,
    val relativePath: String?,
    val size: Long,
    val lastModified: String?,
    val updatedAtMillis: Long
)
