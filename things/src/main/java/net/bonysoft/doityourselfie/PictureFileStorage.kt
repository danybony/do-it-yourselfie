package net.bonysoft.doityourselfie

import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

internal fun saveImageToFile(imageBytes: ByteArray?): String? {
    val pictureFile = getOutputMediaFile() ?: return null
    Timber.d("Storing image to ${pictureFile.absolutePath}")
    return try {
        val fos = FileOutputStream(pictureFile)
        fos.write(imageBytes)
        fos.close()
        pictureFile.absolutePath
    } catch (e: IOException) {
        Timber.e(e, "failed to store to file")
        null
    }
}

private fun getOutputMediaFile(): File? {
    val mediaStorageDir = File(
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
        "DoItYourselfie"
    )
    if (!mediaStorageDir.exists()) {
        if (!mediaStorageDir.mkdirs()) {
            Timber.d("failed to create directory")
            return null
        }
    }
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val mediaFile: File
    mediaFile = File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")

    return mediaFile
}
