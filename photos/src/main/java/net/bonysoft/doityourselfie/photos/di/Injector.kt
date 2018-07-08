package net.bonysoft.doityourselfie.photos.di

internal fun createLibraryComponent(isDebug: Boolean): LibraryComponent =
        DaggerLibraryComponent.builder()
                .libraryModule(LibraryModule(isDebug))
                .build()