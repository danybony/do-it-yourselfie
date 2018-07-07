package net.bonysoft.doityourselfie.photos.di

import android.app.Application

internal fun createLibraryComponent(application: Application, oAuth2Token: String) : LibraryComponent =
        DaggerLibraryComponent.builder()
                .libraryModule(LibraryModule(application, oAuth2Token))
                .build()