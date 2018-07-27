package net.bonysoft.doityourselfie.photos.di

import android.app.Application

internal fun createLibraryComponent(application: Application, isDebug: Boolean): LibraryComponent =
        DaggerLibraryComponent.builder()
                .libraryModule(LibraryModule(application, isDebug))
                .build()