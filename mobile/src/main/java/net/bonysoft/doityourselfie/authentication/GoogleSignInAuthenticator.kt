package net.bonysoft.doityourselfie.authentication

import android.app.Activity
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import net.bonysoft.doityourselfie.R


class GoogleSignInAuthenticator<T> private constructor(private val host: T) : LifecycleObserver
        where T : AppCompatActivity, T : AuthenticationListener {

    companion object {
        fun <T> attachTo(host: T) where T : AppCompatActivity, T : AuthenticationListener =
                GoogleSignInAuthenticator(host).apply {
                    host.lifecycle.addObserver(this)
                }

        private const val AUTHENTICATION_CODE = 1234
        private const val PHOTOS_READ_APPEND_SCOPE = "https://www.googleapis.com/auth/photoslibrary"
        private const val PHOTOS_SHARING_SCOPE = "https://www.googleapis.com/auth/photoslibrary.sharing"
    }

    private val oAuthSecret = host.getString(R.string.google_id_client_oauth)

    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(Scope(PHOTOS_SHARING_SCOPE), Scope(PHOTOS_READ_APPEND_SCOPE))
//            .requestServerAuthCode(oAuthSecret)
            .requestIdToken(oAuthSecret)
            .build()

    private val client = GoogleSignIn.getClient(host, gso)

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        val previousAccount = GoogleSignIn.getLastSignedInAccount(host)
        if (previousAccount != null) {
            dispatchToken(previousAccount)
        } else {
            host.showLoggedOutUi()
        }
    }

    private fun dispatchToken(account: GoogleSignInAccount) {
//        host.showLoggedUi(account.serverAuthCode)
        host.showLoggedUi(account.idToken)
    }

    fun shouldParseResult(requestCode: Int) = requestCode == AUTHENTICATION_CODE

    fun parseResult(data: Intent) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            dispatchToken(account)
        } catch (e: ApiException) {
            host.showLoggedOutUi()
        }
    }

    fun authenticate() {
        host.startActivityForResult(client.signInIntent, AUTHENTICATION_CODE)
    }

    fun logout() {
        client.signOut()
        host.showLoggedOutUi()
    }
}