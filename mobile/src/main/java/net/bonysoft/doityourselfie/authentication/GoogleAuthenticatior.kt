package net.bonysoft.doityourselfie.authentication

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class GoogleAuthenticatior<T> private constructor(private val host: T) : LifecycleObserver
        where T : AppCompatActivity, T : AuthenticationListener {

    companion object {
        fun <T> attachTo(host: T) where T : AppCompatActivity, T : AuthenticationListener =
                GoogleAuthenticatior(host).apply {
                    host.lifecycle.addObserver(this)
                }

        private const val AUTHENTICATION_CODE = 1234
    }

    private val auth = FirebaseAuth.getInstance()
    private val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
    )

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (auth.currentUser != null) {
            dispatchToken()
        } else {
            host.showLoggedOutUi()
        }
    }

    private fun dispatchToken() =
            host.showLoggedUi(
                    auth.currentUser?.getIdToken(false)?.getResult()?.getToken().toString()
            )

    fun shouldParseResult(requestCode: Int) = requestCode == AUTHENTICATION_CODE

    fun parseResult(resultCode: Int, intent: Intent) {
        if (resultCode == RESULT_OK) {
            dispatchToken()
        }
    }

    fun authenticate() {
        host.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                AUTHENTICATION_CODE
        )
    }

    fun logout() {
        auth.signOut()
    }
}