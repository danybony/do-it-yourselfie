package net.bonysoft.doityourselfie.photos.di

import android.app.Application
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.readystatesoftware.chuck.ChuckInterceptor
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import net.bonysoft.doityourselfie.photos.BuildConfig
import net.bonysoft.doityourselfie.photos.network.ApiService
import net.bonysoft.doityourselfie.photos.network.UploadApiService
import net.bonysoft.doityourselfie.photos.utils.ImageTransformer
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

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

    @Provides
    fun provideImageTransformer(): ImageTransformer = ImageTransformer(application)

    @Provides
    fun provideUploadApiService(client: OkHttpClient): UploadApiService =
            UploadApiService(client, BuildConfig.API_ENDPOINT)
}