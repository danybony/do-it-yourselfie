package net.bonysoft.doityourselfie


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import net.bonysoft.doityourselfie.authentication.AuthenticationListener
import net.bonysoft.doityourselfie.authentication.GoogleSignInAuthenticator
import net.bonysoft.doityourselfie.photos.PhotosAPI
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum
import net.bonysoft.doityourselfie.ui.AlbumAdapter
import net.bonysoft.doityourselfie.ui.AlbumSelectedListener

class MainActivity : AppCompatActivity(), AuthenticationListener, AlbumSelectedListener {

    private lateinit var authenticator: GoogleSignInAuthenticator<MainActivity>
    private lateinit var photosAPI: PhotosAPI

    private val adapter by lazy { AlbumAdapter(this, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        authenticator = GoogleSignInAuthenticator.attachTo(this)

        albumList.let {
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this)
        }

        albumView.setListener(this)

        showLoggedOutUi()
    }

    override fun showLoggedUi(token: String?) {
        loggedInUi.visibility = View.VISIBLE
        loggedOutUi.visibility = View.GONE
        btnLogout.setOnClickListener {
            authenticator.logout()
        }

        photosAPI = PhotosAPI(application, token!!, BuildConfig.DEBUG)
        Hawk.put(TOKEN_KEY, token)
        albumName.setText("Test Album 001")

        btnCreateAlbum.setOnClickListener {
            createAlbum(albumName.text.toString())
        }

        btnListAlbums.setOnClickListener {
            fetchAlbums()
        }
    }

    private fun createAlbum(albumName: String) {
        launch(UI) {
            waitingUi.visibility = View.VISIBLE
            listUi.visibility = View.GONE
            singleAlbumUi.visibility = View.GONE
            try {
                val response = photosAPI.createAlbum(albumName).await()
                albumView.bindTo(response)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                waitingUi.visibility = View.GONE
                singleAlbumUi.visibility = View.VISIBLE
            }
        }
    }

    private fun fetchAlbums() {
        launch(UI) {
            waitingUi.visibility = View.VISIBLE
            listUi.visibility = View.GONE
            singleAlbumUi.visibility = View.GONE
            try {
                val list = photosAPI.fetchAlbums().await()
                adapter.addAlbums(list)
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                waitingUi.visibility = View.GONE
                listUi.visibility = View.VISIBLE
            }
        }
    }

    override fun showLoggedOutUi() {
        loggedInUi.visibility = View.GONE
        loggedOutUi.visibility = View.VISIBLE
        listUi.visibility = View.GONE
        singleAlbumUi.visibility = View.GONE
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

    override fun onAlbumSelected(completeAlbum: CompleteAlbum) {
        PhotoLoadingActivity.showAlbumDetails(this, completeAlbum)
    }
}
