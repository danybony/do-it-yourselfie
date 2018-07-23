package net.bonysoft.doityourselfie.photos.network

import kotlinx.coroutines.experimental.Deferred
import net.bonysoft.doityourselfie.photos.model.*
import net.bonysoft.doityourselfie.photos.utils.ImageTransformer.Companion.OCTET_TYPE
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

internal interface ApiService {

    @POST("albums")
    fun createAlbum(@Header("Authorization") token: String,
                    @Body albumRequest: AlbumRequest): Deferred<AlbumResponse>

    @GET("albums")
    fun fetchAlbums(@Header("Authorization") token: String,
                    @Query("pageToken") nextPageToken: String? = null): Deferred<AlbumListResponse>

    @POST("uploads")
    fun uploadMedia(@Header("Authorization") token: String,
                    @Header("X-Goog-Upload-File-Name") fileName: String,
                    @Body bytes: ByteArray,
                    @Header("Content-Type") contentType: String = OCTET_TYPE): Deferred<ResponseBody>

    @Multipart
    @POST("uploads")
    fun uploadMedia(@Header("Authorization") token: String,
                    @Header("X-Goog-Upload-File-Name") fileName: String,
                    @Part image: MultipartBody.Part): Deferred<ResponseBody>

    @POST("./mediaItems:batchCreate")
    fun createMediaLink(@Header("Authorization") token: String,
                        @Body request: ImageUploadRequest): Deferred<ImageUploadResult>

    @POST("./mediaItems:search")
    fun listPicturesInAlbum(@Header("Authorization") token: String,
                            @Query("albumId") albumId: String,
                            @Query("pageSize") pageSize: Int = 100,
                            @Query("pageToken") nextPageToken: String? = null): Deferred<PhotoListResponse>
}