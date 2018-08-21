package net.bonysoft.doityourselfie

import net.bonysoft.doityourselfie.queue.Picture
import net.bonysoft.doityourselfie.queue.QueueDatabase


class PicturesUploadQueue(private val database: QueueDatabase) {

    fun put(picturePath: String) {
        val picture = Picture().apply { this.imageUrl = picturePath }
        database.pictureDao().put(picture)
    }

    fun picturesToUpload(): List<Picture> {
        return database.pictureDao().queuedPictures()
    }

    fun markPictureAsUploaded(picturePath: String) {
        val picture = Picture().apply {
            this.imageUrl = picturePath
            this.uploaded = 1
        }
        database.pictureDao().update(picture)
    }

}
