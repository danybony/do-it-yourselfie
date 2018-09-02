package net.bonysoft.doityourselfie.photos.internal

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum
import net.bonysoft.doityourselfie.photos.model.ImageUploadResult
import net.bonysoft.doityourselfie.photos.model.MediaItem
import net.bonysoft.doityourselfie.photos.network.ApiService
import net.bonysoft.doityourselfie.photos.network.UploadApiService
import net.bonysoft.doityourselfie.photos.utils.asImageUploadRequestWith
import net.bonysoft.doityourselfie.photos.utils.replaceWith
import net.bonysoft.doityourselfie.photos.utils.toAlbumRequest

internal class PhotosApiImpl(private val apiService: ApiService,
                             private val uploadApiService: UploadApiService,
                             private val tokenBearer: String) {

    internal fun createAlbum(albumName: String) =
            apiService.createAlbum(tokenBearer, albumName.toAlbumRequest())

    internal fun fetchAlbums(): Deferred<ArrayList<CompleteAlbum>> {
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

    //imageTransformer.toByteArray(bitmap, fileName)
    internal fun uploadImage(albumId: String, fileName: String, bytes: ByteArray): Deferred<ImageUploadResult> {
        return async {
            val token = uploadApiService.uploadMedia(tokenBearer, fileName, bytes).await()
            apiService.createMediaLink(tokenBearer, token.asImageUploadRequestWith(albumId, fileName)).await()
        }
    }

    internal fun fetchPicturesInAlbum(albumId: String, pageSize: Int = 100): Deferred<ArrayList<MediaItem>> {
        return async {
            val items = arrayListOf<MediaItem>()
            var nextPageToken: String? = null

            do {
                val page = apiService.listPicturesInAlbum(tokenBearer, albumId, pageSize, nextPageToken).await()
                nextPageToken = nextPageToken.replaceWith(page.nextPageToken)
                if (page.mediaItems != null) {
                    items.addAll(page.mediaItems)
                }
            } while (nextPageToken != null && nextPageToken.isNotEmpty())
            return@async items
        }
    }
}