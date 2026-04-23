package com.example.foodpics.feature.viewer

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SaveImageUseCase(private val context: Context) {

    suspend fun save(bitmap: Bitmap): Uri = withContext(Dispatchers.IO) {
        val filename = "FoodPics_${System.currentTimeMillis()}.jpg"
        val resolver = context.contentResolver

        val pendingValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/FoodPics",
            )
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, pendingValues)
            ?: error("MediaStore insert returned null")

        try {
            resolver.openOutputStream(uri)?.use { out ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    error("Bitmap.compress returned false")
                }
            } ?: error("Unable to open output stream for $uri")

            val finalValues = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            resolver.update(uri, finalValues, null, null)
            uri
        } catch (t: Throwable) {
            runCatching { resolver.delete(uri, null, null) }
            throw t
        }
    }
}
