package net.bonysoft.doityourselfie.photos.di

import android.app.Application

internal fun createLibraryComponent(application: Application,
                                    oAuth2Token: String,
                                    isDebug: Boolean): LibraryComponent =
        DaggerLibraryComponent.builder()
                .libraryModule(LibraryModule(application, oAuth2Token, isDebug))
                .build()