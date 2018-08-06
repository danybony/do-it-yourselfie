package net.bonysoft.doityourselfie.authentication

import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException

interface AuthenticationListener {

    fun showLoggedUi(token: String?)

    fun showLoggedOutUi()

    fun onUserRecoverableException(e: UserRecoverableAuthIOException)
}