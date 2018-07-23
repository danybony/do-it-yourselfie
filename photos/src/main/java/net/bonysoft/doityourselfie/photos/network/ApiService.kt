package net.bonysoft.doityourselfie.photos.network

import kotlinx.coroutines.experimental.Deferred
import net.bonysoft.doityourselfie.photos.model.AlbumListResponse
import net.bonysoft.doityourselfie.photos.model.AlbumRequest
import net.bonysoft.doityourselfie.photos.model.AlbumResponse
import net.bonysoft.doityourselfie.photos.model.ImageUploadRequest
import okhttp3.ResponseBody
import retrofit2.http.*

internal interface ApiService {

    @POST("albums")
    fun createAlbum(@Header("Authorization") token: String,
                    @Body albumRequest: AlbumRequest): Deferred<AlbumResponse>

    @GET("albums")
    fun fetchAlbums(@Header("Authorization") token: String,
                    @Header("nextPageToken") nextPageToken: String? = null): Deferred<AlbumListResponse>

    @POST("uploads")
    fun uploadMedia(@Header("Authorization") token: String,
                    @Header("X-Goog-Upload-File-Name") fileName: String,
                    @Body bytes: ByteArray): Deferred<ResponseBody>

    @POST("./mediaItems:batchCreate")
    fun createMediaLink(@Header("Authorization") token: String,
                        @Body request: ImageUploadRequest): Deferred<ResponseBody>
}