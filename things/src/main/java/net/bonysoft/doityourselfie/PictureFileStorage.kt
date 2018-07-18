package net.bonysoft.doityourselfie

import android.os.Environment
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

internal fun saveImageToFile(imageBytes: ByteArray?) {
    val pictureFile = getOutputMediaFile() ?: return
    Timber.d("Storing image to ${pictureFile.absolutePath}")
    try {
        val fos = FileOutputStream(pictureFile)
        fos.write(imageBytes)
        fos.close()
    } catch (e: IOException) {
        Timber.e(e, "failed to store to file")
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
    // Create a media file name
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val mediaFile: File
    mediaFile = File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")

    return mediaFile
}
