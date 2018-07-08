package net.bonysoft.doityourselfie.photos

import android.app.Application
import net.bonysoft.doityourselfie.photos.di.createLibraryComponent
import net.bonysoft.doityourselfie.photos.utils.toAlbumRequest

class PhotosAPI(application: Application,
                oAuth2Token: String,
                isDebug: Boolean = false) {

    private val apiService = createLibraryComponent(application, oAuth2Token, isDebug).apiService()

    fun createAlbum(albumName: String) =
            apiService.createAlbum(albumName.toAlbumRequest())
}