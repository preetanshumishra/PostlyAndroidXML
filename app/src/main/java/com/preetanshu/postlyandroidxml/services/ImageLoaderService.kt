package com.preetanshu.postlyandroidxml.services

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

interface ImageLoaderService {
    suspend fun loadBitmap(url: String): Bitmap?
}

class ImageLoaderServiceImplementation : ImageLoaderService {

    private val cache: LruCache<String, Bitmap> = run {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    override suspend fun loadBitmap(url: String): Bitmap? {
        cache.get(url)?.let { return it }

        return withContext(Dispatchers.IO) {
            val connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15_000
                readTimeout = 15_000
                doInput = true
            }

            try {
                if (connection.responseCode !in 200..299) {
                    return@withContext null
                }

                connection.inputStream.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }?.also { bitmap ->
                    cache.put(url, bitmap)
                }
            } catch (_: Exception) {
                null
            } finally {
                connection.disconnect()
            }
        }
    }
}
