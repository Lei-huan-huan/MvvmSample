package com.lhh.mvvmsample.data.download

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.lhh.mvvmsample.data.local.ImageEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.Request

@Singleton
class GalleryImageWriter @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val okHttpClient: OkHttpClient,
) {

    fun saveFromNetwork(entity: ImageEntity) {
        val request = Request.Builder().url(entity.url).build()
        okHttpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code}: ${entity.name}")
            }
            val body = response.body ?: throw IOException("无响应内容: ${entity.name}")
            val bytes = body.bytes()
            val mime =
                body.contentType()?.toString()?.substringBefore(';')?.trim()?.takeIf { it.isNotEmpty() }
                    ?: guessMimeType(entity.name)
            insertIntoGallery(entity.displayFileName(), mime, bytes)
        }
    }

    private fun ImageEntity.displayFileName(): String {
        val base = name.trim().ifEmpty { "image" }
        return if ('.' in base) base else "$base.jpg"
    }

    private fun guessMimeType(fileName: String): String {
        val ext = fileName.substringAfterLast('.', "").lowercase()
        if (ext.isEmpty()) return "image/jpeg"
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext) ?: "image/jpeg"
    }

    private fun insertIntoGallery(displayName: String, mimeType: String, bytes: ByteArray) {
        val resolver = appContext.contentResolver
        val values =
            ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + "/MvvmImageClient",
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
            }
        val uri =
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw IOException("无法写入相册")
        try {
            resolver.openOutputStream(uri)?.use { out -> out.write(bytes) }
                ?: throw IOException("无法打开相册输出流")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            }
        } catch (e: Exception) {
            try {
                resolver.delete(uri, null, null)
            } catch (_: Exception) {
            }
            throw e
        }
    }
}
