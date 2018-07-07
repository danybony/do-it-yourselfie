package net.bonysoft.doityourselfie


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.bonysoft.doityourselfie.authentication.AuthenticationListener
import net.bonysoft.doityourselfie.authentication.GoogleAuthenticator

class MainActivity : AppCompatActivity(), AuthenticationListener{

    private lateinit var authenticator: GoogleAuthenticator<MainActivity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        authenticator = GoogleAuthenticator.attachTo(this)
    }

    override fun showLoggedUi(token: String?) {
        btnLogout.visibility = View.VISIBLE
        btnLogin.visibility = View.GONE
        btnLogout.setOnClickListener {
            authenticator.logout()
        }
    }

    override fun showLoggedOutUi() {
        btnLogout.visibility = View.GONE
        btnLogin.visibility = View.VISIBLE
        btnLogin.setOnClickListener {
            authenticator.authenticate()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (authenticator.shouldParseResult(requestCode)) {
            authenticator.parseResult(resultCode)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
