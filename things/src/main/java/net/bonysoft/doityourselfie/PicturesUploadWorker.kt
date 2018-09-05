package net.bonysoft.doityourselfie

import android.app.Application
import android.arch.persistence.room.Room
import android.graphics.BitmapFactory
import androidx.work.Data
import androidx.work.Worker
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.experimental.runBlocking
import net.bonysoft.doityourselfie.MainActivity.Companion.TOKEN_KEY
import net.bonysoft.doityourselfie.photos.PhotosAPI
import net.bonysoft.doityourselfie.queue.QueueDatabase
import retrofit2.HttpException
import timber.log.Timber

const val KEY_FAILURE_REASON = "failure_reason"

enum class FailureReason {
    AUTH,
    NETWORK
}

class PicturesUploadWorker : Worker() {

    override fun doWork(): Result {
        if (!Hawk.contains(TOKEN_KEY)) {
            outputData = Data.Builder().putString(KEY_FAILURE_REASON, FailureReason.AUTH.name).build()
            return Result.FAILURE
        }

        val application = applicationContext as Application
        val photosAPI = PhotosAPI(application, Hawk.get(TOKEN_KEY), BuildConfig.DEBUG)

        val queueDatabase = Room.databaseBuilder(applicationContext, QueueDatabase::class.java, "pictures_queue").build()
        val picturesUploadQueue = PicturesUploadQueue(queueDatabase)
        val picturesToUpload = picturesUploadQueue.picturesToUpload()

        var allUploadsSuccessful = true

        Timber.d("Pictures to be uploaded: ${picturesToUpload.size}")
        runBlocking {
            val albumId = photosAPI.fetchAlbums().await()
                .first { it.title.contains(BuildConfig.ALBUM_KEYWORD, true) }
                .id
            for (picture in picturesToUpload) {
                Timber.d("Uploading ${picture.imageFile}")
                try {
                    val bitmap = BitmapFactory.decodeFile(picture.imageFile)
                    val results = photosAPI.uploadImage(albumId, picture.imageFile.extractName(), bitmap).await()
                    val errors = results.newMediaItemResults.filter { it.status.code != 0 }

                    if (errors.isNotEmpty()) {
                        for (error in errors) {
                            Timber.e("Error uploading picture, code: ${error.status}")
                        }
                        allUploadsSuccessful = false
                    } else {
                        Timber.d("Upload completed")
                        picturesUploadQueue.markPictureAsUploaded(picture.imageFile)
                    }
                } catch (e: HttpException) {
                    val reason = if (e.code() == 401) {
                        FailureReason.AUTH
                    } else {
                        FailureReason.NETWORK
                    }
                    outputData = Data.Builder().putString(KEY_FAILURE_REASON, reason.name).build()
                    Timber.e(e)
                    allUploadsSuccessful = false
                    break
                } catch (e: Exception) {
                    Timber.e(e)
                    allUploadsSuccessful = false
                    break
                }
            }
        }

        return if (allUploadsSuccessful) {
            Result.SUCCESS
        } else {
            Result.FAILURE
        }
    }

}

private fun String.extractName() = this.split("/").last()
