package net.bonysoft.doityourselfie.photos.di

import android.app.Application
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import net.bonysoft.doityourselfie.photos.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
internal class LibraryModule(val application: Application,
                             val oAuth2Token: String) {

    @Provides
    fun provideMoshi(): Moshi =
            Moshi.Builder().build()

    @Provides
    fun provideRetrofit(moshi: Moshi) : Retrofit =
            Retrofit.Builder()
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .addCallAdapterFactory(CoroutineCallAdapterFactory())
                    .baseUrl(BuildConfig.API_ENDPOINT)
                    .build()

}