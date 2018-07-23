package net.bonysoft.doityourselfie.photos.di

import dagger.Component
import net.bonysoft.doityourselfie.photos.network.ApiService
import net.bonysoft.doityourselfie.photos.utils.ImageTransformer

@Component(modules = [(LibraryModule::class)])
internal interface LibraryComponent {

    fun apiService() : ApiService

    fun imageTransformer() : ImageTransformer
}