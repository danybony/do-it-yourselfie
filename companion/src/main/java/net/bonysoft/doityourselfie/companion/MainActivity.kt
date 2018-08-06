package net.bonysoft.doityourselfie.companion

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*


import net.bonysoft.doityourselfie.authentication.AuthenticationListener
import net.bonysoft.doityourselfie.authentication.GoogleSignInAuthenticator

class MainActivity : AppCompatActivity(), AuthenticationListener {

    private lateinit var authenticator: GoogleSignInAuthenticator<MainActivity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        authenticator = GoogleSignInAuthenticator.attachTo(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (authenticator.shouldParseResult(requestCode)) {
            authenticator.parseResult(data!!)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun showLoggedUi(token: String?) {
        loggedInUi.visibility = View.VISIBLE
        loggedOutUi.visibility = View.GONE
        btnLogout.setOnClickListener {
            authenticator.logout()
        }
    }

    override fun showLoggedOutUi() {
        loggedInUi.visibility = View.GONE
        loggedOutUi.visibility = View.VISIBLE
        btnLogin.setOnClickListener {
            authenticator.authenticate()
        }
    }

    override fun onUserRecoverableException(e: Exception) {

    }

}
