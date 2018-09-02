package net.bonysoft.doityourselfie.photos

import android.app.Application
import android.graphics.Bitmap
import kotlinx.coroutines.experimental.Deferred
import net.bonysoft.doityourselfie.photos.di.createLibraryComponent
import net.bonysoft.doityourselfie.photos.internal.PhotosApiImpl
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum
import net.bonysoft.doityourselfie.photos.model.ImageUploadResult

class PhotosAPI(application: Application,
                oAuth2Token: String,
                isDebug: Boolean = false) {

    private val injector = createLibraryComponent(application, isDebug)
    private val imageTransformer = injector.imageTransformer()
    private val photoApi = PhotosApiImpl(
            injector.apiService(),
            injector.uploadApiService(),
            "Bearer $oAuth2Token")

    fun createAlbum(albumName: String) = photoApi.createAlbum(albumName)

    fun fetchAlbums() = photoApi.fetchAlbums()

    fun uploadImage(album: CompleteAlbum, fileName: String, bitmap: Bitmap): Deferred<ImageUploadResult> {
        return uploadImage(album.id, fileName, bitmap)
    }

    fun uploadImage(albumId: String, fileName: String, bitmap: Bitmap) =
            photoApi.uploadImage(albumId, fileName, imageTransformer.toByteArray(bitmap, fileName))

    fun fetchPicturesInAlbum(album: CompleteAlbum, pageSize: Int = 100) =
            photoApi.fetchPicturesInAlbum(albumId = album.id, pageSize = pageSize)
}
