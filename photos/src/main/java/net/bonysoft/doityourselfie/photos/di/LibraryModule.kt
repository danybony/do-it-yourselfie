package net.bonysoft.doityourselfie.photos.di

import android.app.Application
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import net.bonysoft.doityourselfie.photos.BuildConfig
import net.bonysoft.doityourselfie.photos.network.API_ENDPOINT
import net.bonysoft.doityourselfie.photos.network.ApiService
import net.bonysoft.doityourselfie.photos.network.UploadApiService
import net.bonysoft.doityourselfie.photos.network.createRetrofit
import net.bonysoft.doityourselfie.photos.utils.ImageTransformer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Named

@Module
internal class LibraryModule(private val application: Application,
                             private val isDebug: Boolean) {

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
                    .addInterceptor(ChuckInterceptor(application))
                    .build()

    @Provides
    @Named("Endpoint")
    fun provideEndpoint() = API_ENDPOINT

    @Provides
    fun provideApiService(moshi: Moshi,
                          client: OkHttpClient,
                          @Named("Endpoint") endpoint: String): ApiService =
            createRetrofit(moshi, client, endpoint)

    @Provides
    fun provideImageTransformer(): ImageTransformer = ImageTransformer()

    @Provides
    fun provideUploadApiService(client: OkHttpClient): UploadApiService =
            UploadApiService(client, BuildConfig.API_ENDPOINT)
}