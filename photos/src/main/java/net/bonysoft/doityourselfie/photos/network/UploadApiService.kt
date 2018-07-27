package net.bonysoft.doityourselfie.photos.network

import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.async
import okhttp3.*

internal class UploadApiService(private val client: OkHttpClient,
                                root: String = API_ENDPOINT) {

    private val endpoint = "${root}uploads"
    private val contentType = MediaType.parse(OCTET_TYPE)


    internal fun uploadMedia(token: String,
                             fileName: String,
                             bytes: ByteArray): Deferred<String> {

        return async {
            val request = requestBuilderFor(token, fileName)
                    .post(RequestBody.create(contentType, bytes))
                    .build()

            client.newCall(request).execute().body()!!.string()
        }
    }

    private fun requestBuilderFor(token: String,
                                  fileName: String): Request.Builder =
            Request.Builder()
                    .url(endpoint)
                    .addHeader(AUTHORIZATION, token)
                    .addHeader(UPLOAD_FILENAME, fileName)


}