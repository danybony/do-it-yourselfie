package net.bonysoft.doityourselfie.companion

import android.app.Application
import com.orhanobut.hawk.Hawk
import timber.log.Timber

class CompanionApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        Hawk.init(this).build()
    }
}