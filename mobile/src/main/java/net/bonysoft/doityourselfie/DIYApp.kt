package net.bonysoft.doityourselfie

import android.app.Application
import timber.log.Timber

class DIYApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}