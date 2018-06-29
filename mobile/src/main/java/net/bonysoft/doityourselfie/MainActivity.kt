package net.bonysoft.doityourselfie


import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.bonysoft.doityourselfie.authentication.AuthenticationListener
import net.bonysoft.doityourselfie.authentication.GoogleAuthenticatior

class MainActivity : AppCompatActivity(), AuthenticationListener{

    private lateinit var authenticatior: GoogleAuthenticatior<MainActivity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authenticatior = GoogleAuthenticatior.attachTo(this)
    }

    override fun showLoggedUi(token: String) {
        btnLogout.visibility = View.VISIBLE
        btnLogin.visibility = View.GONE
        btnLogout.setOnClickListener {
            authenticatior.logout()
        }
    }

    override fun showLoggedOutUi() {
        btnLogout.visibility = View.GONE
        btnLogin.visibility = View.VISIBLE
        btnLogout.setOnClickListener {
            authenticatior.authenticate()
        }
    }
}
