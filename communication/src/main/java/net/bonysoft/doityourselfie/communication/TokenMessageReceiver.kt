package net.bonysoft.doityourselfie.communication

import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.nearby.messages.MessageListener

internal class TokenMessageReceiver(private val tokenReceiver: TokenReceiver) : MessageListener() {

    override fun onFound(message: Message?) {
        message?.let {
            tokenReceiver.onTokenReceived(String(it.content))
        }
    }
}