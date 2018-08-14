package net.bonysoft.doityourselfie.communication

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.nearby.Nearby

class TokenManager private constructor(private val activity: AppCompatActivity,
                                       tokenReceiver: TokenReceiver) : LifecycleObserver {

    private val tokenMessageReceiver = TokenMessageReceiver(tokenReceiver)

    companion object {
        fun <T> attachTo(host: T) where T : AppCompatActivity, T : TokenReceiver {
            attachTo(host, host)
        }

        fun attachTo(appCompatActivity: AppCompatActivity, tokenReceiver: TokenReceiver) {
            TokenManager(appCompatActivity, tokenReceiver).let {
                appCompatActivity.lifecycle.addObserver(it)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onLifecycleStart() {
        Nearby.getMessagesClient(activity).subscribe(tokenMessageReceiver)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onLifecycleStop() {
        Nearby.getMessagesClient(activity).unsubscribe(tokenMessageReceiver)
    }
}