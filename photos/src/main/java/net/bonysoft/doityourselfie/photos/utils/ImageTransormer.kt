package net.bonysoft.doityourselfie.photos.utils

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class ImageTransormer {

    companion object {
        private const val IMAGE_QUALITY = 100

        private const val JPG = ".jpg"
        private const val JPEG = ".jpeg"
        private const val PNG = ".png"
        private const val WEBP = ".webp"
    }

    fun toByteArray(bitmap: Bitmap, fileName: String): ByteArray =
            ByteArrayOutputStream().apply {
                bitmap.compress(compressFormatFor(fileName), IMAGE_QUALITY, this)
            }.toByteArray()

    private fun compressFormatFor(fileName: String): Bitmap.CompressFormat =
            when {
                fileName.isJPG() -> Bitmap.CompressFormat.JPEG
                fileName.isWEBP() -> Bitmap.CompressFormat.WEBP
                else -> Bitmap.CompressFormat.PNG
            }

    private fun String.isJPG() = endsWith(JPG, true) or endsWith(JPEG, true)

    private fun String.isWEBP() = endsWith(WEBP, true)
}