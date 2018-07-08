package net.bonysoft.doityourselfie.photos.di

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import net.bonysoft.doityourselfie.photos.BuildConfig
import net.bonysoft.doityourselfie.photos.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
internal class LibraryModule(private val isDebug: Boolean) {

    @Provides
    fun provideMoshi(): Moshi =
            Moshi.Builder().build()

    @Provides
    fun provideLoggingLevel() =
            if (isDebug) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }

    @Provides
    fun provideLoggingInterceptor(level: HttpLoggingInterceptor.Level): HttpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(level)

    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()

    @Provides
    fun provideRetrofit(moshi: Moshi, client: OkHttpClient): Retrofit =
            Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .baseUrl(BuildConfig.API_ENDPOINT)
                    .client(client)
                    .build()

    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService =
            retrofit.create(ApiService::class.java)
}