package net.bonysoft.doityourselfie.photos

import android.app.Application
import net.bonysoft.doityourselfie.photos.di.createLibraryComponent

class PhotosAPI(application: Application,
                oAuth2Token: String,
                isDebug: Boolean = false) {

    private val apiService = createLibraryComponent(application, oAuth2Token, isDebug).apiService()

}