package net.bonysoft.doityourselfie.photos.di

import dagger.Component
import net.bonysoft.doityourselfie.photos.network.ApiService

@Component(modules = [(LibraryModule::class)])
internal interface LibraryComponent {

    fun apiService() : ApiService
}