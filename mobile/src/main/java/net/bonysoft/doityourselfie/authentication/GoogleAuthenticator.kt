package net.bonysoft.doityourselfie.authentication

import android.app.Activity.RESULT_OK
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.v7.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth

class GoogleAuthenticator<T> private constructor(private val host: T) : LifecycleObserver
        where T : AppCompatActivity, T : AuthenticationListener {

    companion object {
        fun <T> attachTo(host: T) where T : AppCompatActivity, T : AuthenticationListener =
                GoogleAuthenticator(host).apply {
                    host.lifecycle.addObserver(this)
                }

        private const val AUTHENTICATION_CODE = 1234
        private const val PHOTOS_READ_APPEND_SCOPE = "https://www.googleapis.com/auth/photoslibrary"
        private const val PHOTOS_SHARING_SCOPE = "https://www.googleapis.com/auth/photoslibrary.sharing"
    }

    private val auth = FirebaseAuth.getInstance()
    private val scopes = arrayListOf(
            PHOTOS_READ_APPEND_SCOPE,
            PHOTOS_SHARING_SCOPE
    )
    private val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().setScopes(scopes).build()
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
            auth.currentUser?.getIdToken(false)?.addOnCompleteListener {
                host.showLoggedUi(it.result.token)
            }

    fun shouldParseResult(requestCode: Int) = requestCode == AUTHENTICATION_CODE

    fun parseResult(resultCode: Int) {
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
        auth.addAuthStateListener {
            if (it.currentUser == null) {
                host.showLoggedOutUi()
            }
        }
        auth.signOut()
    }
}