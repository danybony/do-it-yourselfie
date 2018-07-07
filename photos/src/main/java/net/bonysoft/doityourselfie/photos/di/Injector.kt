package net.bonysoft.doityourselfie.photos.di

import android.app.Application

internal fun createLibraryComponent(application: Application) : LibraryComponent =
        DaggerLibraryComponent.builder()
                .libraryModule(LibraryModule(application))
                .build()