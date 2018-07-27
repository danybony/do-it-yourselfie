package net.bonysoft.doityourselfie.photos.network

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import net.bonysoft.doityourselfie.photos.BuildConfig
import net.bonysoft.doityourselfie.photos.utils.ImageTransformer.Companion.OCTET_TYPE
import okhttp3.*

class UploadApiService(private val client: OkHttpClient,
                       root: String = BuildConfig.API_ENDPOINT) {

    private val endpoint = "${root}uploads"
    private val contentType = MediaType.parse(OCTET_TYPE)


    fun uploadMedia(token: String,
                    fileName: String,
                    bytes: ByteArray): Deferred<ResponseBody> {

        return async {
            val request = requestBuilderFor(token, fileName)
                    .post(RequestBody.create(contentType, bytes))
                    .build()

            client.newCall(request).execute().body()!!
        }
    }

    private fun requestBuilderFor(token: String,
                                  fileName: String): Request.Builder =
            Request.Builder()
                    .url(endpoint)
                    .addHeader("Authorization", token)
                    .addHeader("X-Goog-Upload-File-Name", fileName)


//    @Multipart
//    @POST("uploads")
//    fun uploadMedia(@Header("Authorization") token: String,
//                    @Header("X-Goog-Upload-File-Name") fileName: String,
//                    @Part image: MultipartBody.Part): Deferred<ResponseBody> {
//
//    }

}