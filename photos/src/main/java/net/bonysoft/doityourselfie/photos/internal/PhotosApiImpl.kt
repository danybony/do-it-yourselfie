package net.bonysoft.doityourselfie.photos.internal

import net.bonysoft.doityourselfie.photos.network.ApiService
import net.bonysoft.doityourselfie.photos.network.UploadApiService
import net.bonysoft.doityourselfie.photos.utils.ImageTransformer
import net.bonysoft.doityourselfie.photos.utils.toAlbumRequest

internal class PhotosApiImpl(private val apiService: ApiService,
                             private val uploadApiService: UploadApiService,
                             private val imageTransformer: ImageTransformer,
                             private val tokenBearer: String) {

    fun createAlbum(albumName: String) =
            apiService.createAlbum(tokenBearer, albumName.toAlbumRequest())
}