package net.bonysoft.doityourselfie.standalone

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.orhanobut.hawk.Hawk
import net.bonysoft.doityourselfie.R

import kotlinx.android.synthetic.main.activity_stand_alone_authentication.*
import net.bonysoft.doityourselfie.TOKEN_KEY
import net.bonysoft.doityourselfie.authentication.AuthenticationListener
import net.bonysoft.doityourselfie.authentication.GoogleSignInAuthenticator
import net.bonysoft.doityourselfie.communication.TokenSender

class StandAloneAuthenticationActivity : AppCompatActivity(), AuthenticationListener {

    private lateinit var authenticator: GoogleSignInAuthenticator<StandAloneAuthenticationActivity>
    private lateinit var tokenSender: TokenSender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stand_alone_authentication)
        setSupportActionBar(toolbar)

        authenticator = GoogleSignInAuthenticator.attachTo(this)

        if (Hawk.contains(TOKEN_KEY)) {
            with(Hawk.get(TOKEN_KEY) as String) {
                tokenSender = TokenSender.attachTo(this@StandAloneAuthenticationActivity, this)
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
            Hawk.delete(TOKEN_KEY)
        }

        btnSendToken.setOnClickListener {
            tokenSender.publishMessage(token!!)
        }

        Hawk.put(TOKEN_KEY, token!!)
        tokenValue.text = token
    }

    override fun showLoggedOutUi() {
        loggedInUi.visibility = View.GONE
        loggedOutUi.visibility = View.VISIBLE
        btnLogin.setOnClickListener {
            authenticator.authenticate()
        }
    }

    override fun onUserRecoverableException(e: Exception) {
        //TODO
    }

}
