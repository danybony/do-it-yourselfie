package net.bonysoft.doityourselfie.communication

interface TokenReceiver {

    fun onTokenReceived(token: String)
}