package net.bonysoft.doityourselfie.photos.utils

import net.bonysoft.doityourselfie.photos.model.*

internal fun String.toAlbumRequest() =
        AlbumRequest(Album(this))

internal fun String.asImageUploadRequestWith(albumId: String, description: String): ImageUploadRequest =
        ImageUploadRequest(albumId, arrayOf(
                NewMediaItem(description, SimpleMediaItem(this))))

internal fun String?.replaceWith(nextPageToken: String?): String? =
        if (this != nextPageToken) {
            nextPageToken
        } else {
            null
        }