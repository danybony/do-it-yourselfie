package net.bonysoft.doityourselfie.companion

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity;
import android.view.View
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*


import net.bonysoft.doityourselfie.authentication.AuthenticationListener
import net.bonysoft.doityourselfie.authentication.GoogleSignInAuthenticator
import net.bonysoft.doityourselfie.communication.TokenSender

class MainActivity : AppCompatActivity(), AuthenticationListener {

    companion object {
        const val TOKEN_KEY = "authentication_token"
    }

    private lateinit var authenticator: GoogleSignInAuthenticator<MainActivity>
    private lateinit var tokenSender: TokenSender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        authenticator = GoogleSignInAuthenticator.attachTo(this)

        if (Hawk.contains(TOKEN_KEY)) {
            with(Hawk.get(TOKEN_KEY) as String) {
                tokenSender = TokenSender.attachTo(this@MainActivity, this)
                showLoggedUi(Hawk.get(TOKEN_KEY))
            }
        } else {
            tokenSender = TokenSender.attachTo(this)
            showLoggedOutUi()
        }
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
            tokenSender.purgeMessage()
        }

        Hawk.put(TOKEN_KEY, token!!)
        tokenSender.publishMessage(token)

        tokenValue.text = token
    }

    override fun showLoggedOutUi() {
        loggedInUi.visibility = View.GONE
        loggedOutUi.visibility = View.VISIBLE
        btnLogin.setOnClickListener {
            authenticator.authenticate()
        }

        Hawk.delete(TOKEN_KEY)
    }

    override fun onUserRecoverableException(e: Exception) {
        //TODO
    }

}
