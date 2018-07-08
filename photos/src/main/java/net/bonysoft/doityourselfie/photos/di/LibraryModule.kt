package net.bonysoft.doityourselfie.photos.di

import android.app.Application
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import net.bonysoft.doityourselfie.photos.BuildConfig
import net.bonysoft.doityourselfie.photos.network.ApiService
import net.bonysoft.doityourselfie.photos.network.AuthenticationInterceptor
import net.bonysoft.doityourselfie.photos.network.TypeIncerceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
internal class LibraryModule(val application: Application,
                             private val oAuth2Token: String,
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
    fun providesAuthenticationInterceptor(): AuthenticationInterceptor =
            AuthenticationInterceptor(oAuth2Token)

    @Provides
    fun provideTypeInterceptor(): TypeIncerceptor =
            TypeIncerceptor()

    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor,
                            authenticationInterceptor: AuthenticationInterceptor,
                            typeInterceptor: TypeIncerceptor): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authenticationInterceptor)
                    .addInterceptor(typeInterceptor)
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