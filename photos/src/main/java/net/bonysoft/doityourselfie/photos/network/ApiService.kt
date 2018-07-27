package net.bonysoft.doityourselfie.photos.network

import kotlinx.coroutines.experimental.Deferred
import net.bonysoft.doityourselfie.photos.model.*
import retrofit2.http.*

internal interface ApiService {

    @POST("albums")
    fun createAlbum(@Header("Authorization") token: String,
                    @Body albumRequest: AlbumRequest): Deferred<AlbumResponse>

    @GET("albums")
    fun fetchAlbums(@Header("Authorization") token: String,
                    @Query("pageToken") nextPageToken: String? = null): Deferred<AlbumListResponse>

    @POST("./mediaItems:batchCreate")
    fun createMediaLink(@Header("Authorization") token: String,
                        @Body request: ImageUploadRequest): Deferred<ImageUploadResult>

    @POST("./mediaItems:search")
    fun listPicturesInAlbum(@Header("Authorization") token: String,
                            @Query("albumId") albumId: String,
                            @Query("pageSize") pageSize: Int = 100,
                            @Query("pageToken") nextPageToken: String? = null): Deferred<PhotoListResponse>
}