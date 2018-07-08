package net.bonysoft.doityourselfie


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import net.bonysoft.doityourselfie.authentication.AuthenticationListener
import net.bonysoft.doityourselfie.authentication.GoogleAuthenticator
import net.bonysoft.doityourselfie.authentication.GoogleSignInAuthenticator
import net.bonysoft.doityourselfie.photos.PhotosAPI
import kotlin.coroutines.experimental.CoroutineContext

class MainActivity : AppCompatActivity(), AuthenticationListener {

//    private lateinit var authenticator: GoogleAuthenticator<MainActivity>
    private lateinit var authenticator: GoogleSignInAuthenticator<MainActivity>
    private lateinit var photosAPI: PhotosAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        authenticator = GoogleSignInAuthenticator.attachTo(this)

        showLoggedOutUi()
    }

    override fun showLoggedUi(token: String?) {
        loggedInUi.visibility = View.VISIBLE
        loggedOutUi.visibility = View.GONE
        btnLogout.setOnClickListener {
            authenticator.logout()
        }

        photosAPI = PhotosAPI(application, token!!, BuildConfig.DEBUG)
        albumName.setText("Test Album 001")

        btnCreateAlbum.setOnClickListener {
            createAlbum(albumName.text.toString())
        }
    }

    private fun createAlbum(albumName: String) {
        launch(UI) {
            waitingUi.visibility = View.VISIBLE
            try {
                val response = photosAPI.createAlbum(albumName).await()
                waitingUi.visibility = View.GONE
                Toast.makeText(this@MainActivity, "SUCCESS: $response", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                waitingUi.visibility = View.GONE
                Toast.makeText(this@MainActivity, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
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
            authenticator.parseResult(data!!)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
