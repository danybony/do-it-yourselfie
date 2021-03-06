package net.bonysoft.doityourselfie.authentication


interface AuthenticationListener {

    fun showLoggedUi(token: String?)

    fun showLoggedOutUi()

    fun onUserRecoverableException(e: Exception)
}