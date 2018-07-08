package net.bonysoft.doityourselfie


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import net.bonysoft.doityourselfie.authentication.AuthenticationListener
import net.bonysoft.doityourselfie.authentication.GoogleAuthenticator
import net.bonysoft.doityourselfie.photos.PhotosAPI

class MainActivity : AppCompatActivity(), AuthenticationListener{

    private lateinit var authenticator: GoogleAuthenticator<MainActivity>
    private lateinit var photosAPI: PhotosAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        authenticator = GoogleAuthenticator.attachTo(this)
    }

    override fun showLoggedUi(token: String?) {
        loggedInUi.visibility = View.VISIBLE
        loggedOutUi.visibility = View.GONE
        btnLogout.setOnClickListener {
            authenticator.logout()
        }

        photosAPI = PhotosAPI(application, token!!, BuildConfig.DEBUG)

        btnCreateAlbum.setOnClickListener {
            createAlbum(albumName.text.toString())
        }
    }

    private fun createAlbum(albumName: String) {

    }

    override fun showLoggedOutUi() {
        loggedInUi.visibility = View.GONE
        loggedOutUi.visibility = View.VISIBLE
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
