package net.bonysoft.doityourselfie.photos.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

internal const val API_ENDPOINT = "https://photoslibrary.googleapis.com/v1/"
internal const val OCTET_TYPE = "application/octet-stream"
internal const val AUTHORIZATION = "Authorization"
internal const val UPLOAD_FILENAME = "X-Goog-Upload-File-Name"

internal fun createRetrofit(moshi: Moshi,
                            client: OkHttpClient,
                            endpoint: String): ApiService =
        Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .baseUrl(endpoint)
                .client(client)
                .build()
                .create(ApiService::class.java)