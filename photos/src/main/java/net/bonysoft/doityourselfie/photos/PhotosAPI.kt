package net.bonysoft.doityourselfie.photos

import android.app.Application
import android.graphics.Bitmap
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import net.bonysoft.doityourselfie.photos.di.createLibraryComponent
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum
import net.bonysoft.doityourselfie.photos.utils.asImageUploadRequestWith
import net.bonysoft.doityourselfie.photos.utils.toAlbumRequest
import java.io.ByteArrayOutputStream

class PhotosAPI(application: Application,
                oAuth2Token: String,
                isDebug: Boolean = false) {

    private val apiService = createLibraryComponent(application, isDebug).apiService()
    private val tokenBearer = "Bearer $oAuth2Token"

    fun createAlbum(albumName: String) =
            apiService.createAlbum(tokenBearer, albumName.toAlbumRequest())

    fun fetchAlbums(): Deferred<ArrayList<CompleteAlbum>> {
        return async {
            val albums = arrayListOf<CompleteAlbum>()
            var nextPageToken: String? = null

            do {
                val page = apiService.fetchAlbums(tokenBearer, nextPageToken).await()
                nextPageToken = page.nextPageToken
                albums.addAll(page.albums)
            } while (nextPageToken != null)
            return@async albums
        }
    }

    fun uploadImage(album: CompleteAlbum, fileName: String, bitmap: Bitmap): Deferred<String> {
        return async {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 1, stream)
            val response = apiService.uploadMedia(tokenBearer, fileName, stream.toByteArray()).await()
            val token = response.string()

            val r2 = apiService.createMediaLink(tokenBearer, token.asImageUploadRequestWith(album.id, fileName)).await()
            r2.string()
        }
    }


}