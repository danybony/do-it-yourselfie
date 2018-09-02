package net.bonysoft.doityourselfie.photos.internal

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum
import net.bonysoft.doityourselfie.photos.network.ApiService
import net.bonysoft.doityourselfie.photos.network.UploadApiService
import net.bonysoft.doityourselfie.photos.utils.ImageTransformer
import net.bonysoft.doityourselfie.photos.utils.replaceWith
import net.bonysoft.doityourselfie.photos.utils.toAlbumRequest

internal class PhotosApiImpl(private val apiService: ApiService,
                             private val uploadApiService: UploadApiService,
                             private val imageTransformer: ImageTransformer,
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
}