package net.bonysoft.doityourselfie.communication

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.Message

class TokenSender private constructor(private val activity: AppCompatActivity,
                                      private var message: Message? = null) : LifecycleObserver {

    companion object {
        fun attachTo(appCompatActivity: AppCompatActivity, token: String? = null): TokenSender {
            val message: Message? = token?.let {
                Message(it.toByteArray())
            }

            return TokenSender(appCompatActivity, message).apply {
                appCompatActivity.lifecycle.addObserver(this)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onLifecycleStart() {
        publishMessage()
    }

    private fun publishMessage() {
        message?.let {
            Nearby.getMessagesClient(activity).publish(it)
        }
    }

    fun publishMessage(token: String) {
        purgeMessageIfPresent()
        message = Message(token.toByteArray())
        publishMessage()
    }

    fun purgeMessage() {
        purgeMessageIfPresent()
        message = null
    }

    private fun purgeMessageIfPresent() {
        message?.let {
            Nearby.getMessagesClient(activity).unpublish(it)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onLifecycleStop() {
        purgeMessageIfPresent()
    }
}