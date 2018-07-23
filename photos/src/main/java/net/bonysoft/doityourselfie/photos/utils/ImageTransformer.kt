package net.bonysoft.doityourselfie.photos.utils

import android.content.Context
import android.graphics.Bitmap
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageTransformer(private val context: Context) {

    companion object {
        private const val IMAGE_QUALITY = 0

        private const val JPG = ".jpg"
        private const val JPEG = ".jpeg"
        private const val WEBP = ".webp"

        internal const val OCTET_TYPE = "application/octet-stream"
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

    fun toMultipart(bitmap: Bitmap, fileName: String): MultipartBody.Part =
            RequestBody.create(MediaType.parse(OCTET_TYPE), bitmap.toFile(fileName)).let {
                MultipartBody.Part.createFormData("", fileName, it)
            }

    private fun Bitmap.toFile(fileName: String): File =
            File(context.cacheDir, fileName).apply {
                with(FileOutputStream(this)) {
                    write(toByteArray(this@toFile, fileName))
                    flush()
                    close()
                }
            }
}