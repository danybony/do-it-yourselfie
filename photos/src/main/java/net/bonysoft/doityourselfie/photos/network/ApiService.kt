package net.bonysoft.doityourselfie.photos.network

import kotlinx.coroutines.experimental.Deferred
import net.bonysoft.doityourselfie.photos.model.AlbumRequest
import net.bonysoft.doityourselfie.photos.model.AlbumResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface ApiService {

    @Headers("token: 1")
    @POST("albums")
    fun createAlbum(@Body albumRequest: AlbumRequest) : Deferred<AlbumResponse>
}