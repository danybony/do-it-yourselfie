package net.bonysoft.doityourselfie.photos

import android.app.Application
import android.graphics.Bitmap
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import net.bonysoft.doityourselfie.photos.di.createLibraryComponent
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum
import net.bonysoft.doityourselfie.photos.utils.toAlbumRequest

class PhotosAPI(application: Application,
                oAuth2Token: String,
                isDebug: Boolean = false) {

    private val apiService = createLibraryComponent(isDebug).apiService()
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

    fun uploadImage(fileName: String, bitmap: Bitmap) {
        launch {
            val token = apiService.uploadMedia(tokenBearer, fileName, bitmap).await()
        }
    }

}