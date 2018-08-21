package net.bonysoft.doityourselfie

import android.arch.persistence.room.Room
import androidx.work.Worker
import net.bonysoft.doityourselfie.queue.QueueDatabase
import timber.log.Timber

class PicturesUploadWorker : Worker() {

    override fun doWork(): Result {

        val queueDatabase = Room.databaseBuilder(applicationContext, QueueDatabase::class.java, "pictures_queue").build()
        val picturesUploadQueue = PicturesUploadQueue(queueDatabase)
        val picturesToUpload = picturesUploadQueue.picturesToUpload()

        var allUploadsSuccessful = true
        for (picture in picturesToUpload) {
            Timber.d("xxx uploading ${picture.imageFile}")
            // TODO do the actual upload and check the result
            if(true) {
                picturesUploadQueue.markPictureAsUploaded(picture.imageFile)
            } else {
                allUploadsSuccessful = false
            }
        }

        return if (allUploadsSuccessful) {
            Result.SUCCESS
        } else {
            Result.RETRY
        }
    }

}
