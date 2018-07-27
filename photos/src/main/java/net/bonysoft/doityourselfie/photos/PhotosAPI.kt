package net.bonysoft.doityourselfie.photos

import android.app.Application
import android.graphics.Bitmap
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import net.bonysoft.doityourselfie.photos.di.createLibraryComponent
import net.bonysoft.doityourselfie.photos.model.*
import net.bonysoft.doityourselfie.photos.utils.asImageUploadRequestWith
import net.bonysoft.doityourselfie.photos.utils.toAlbumRequest

class PhotosAPI(application: Application,
                oAuth2Token: String,
                isDebug: Boolean = false) {

    private val injector = createLibraryComponent(application, isDebug)
    private val apiService = injector.apiService()
    private val imageTransformer = injector.imageTransformer()
    private val uploadApiService = injector.uploadApiService()
    private val tokenBearer = "Bearer $oAuth2Token"

    fun createAlbum(albumName: String) =
            apiService.createAlbum(tokenBearer, albumName.toAlbumRequest())

    fun fetchAlbums(): Deferred<ArrayList<CompleteAlbum>> {
        return async {
            val albums = arrayListOf<CompleteAlbum>()
            var nextPageToken: String? = null

            do {
                val page = apiService.fetchAlbums(tokenBearer, nextPageToken).await()
                nextPageToken = nextPageToken.replaceWith(page.nextPageToken)
                albums.addAll(page.albums)
            } while (nextPageToken != null && nextPageToken.isNotEmpty())
            return@async albums
        }
    }

    private fun String?.replaceWith(nextPageToken: String?): String? =
            if (this != nextPageToken) {
                nextPageToken
            } else {
                null
            }

    fun uploadImage(album: CompleteAlbum, fileName: String, bitmap: Bitmap): Deferred<ImageUploadResult> {
        return async {
            val token = uploadApiService.uploadMedia(tokenBearer, fileName, imageTransformer.toByteArray(bitmap, fileName)).await()
            apiService.createMediaLink(tokenBearer, token.asImageUploadRequestWith(album.id, fileName)).await()
        }
    }

    fun fetchPicturesInAlbum(album: CompleteAlbum, pageSize: Int = 100): Deferred<ArrayList<MediaItem>> {
        return async {
            val items = arrayListOf<MediaItem>()
            var nextPageToken: String? = null

            do {
                val page = apiService.listPicturesInAlbum(tokenBearer, album.id, pageSize, nextPageToken).await()
                nextPageToken = nextPageToken.replaceWith(page.nextPageToken)
                if (page.mediaItems != null) {
                    items.addAll(page.mediaItems)
                }
            } while (nextPageToken != null && nextPageToken.isNotEmpty())
            return@async items
        }
    }
}