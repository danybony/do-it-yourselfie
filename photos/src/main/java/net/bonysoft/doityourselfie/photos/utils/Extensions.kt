package net.bonysoft.doityourselfie.photos.utils

import net.bonysoft.doityourselfie.photos.model.Album
import net.bonysoft.doityourselfie.photos.model.AlbumRequest

internal fun String.toAlbumRequest() =
        AlbumRequest(Album(this))