package net.bonysoft.doityourselfie

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;

import kotlinx.android.synthetic.main.activity_photo_loading.*
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum

class PhotoLoadingActivity : AppCompatActivity() {

    companion object {
        private const val ALBUM_KEY = "net.bonysoft.doityourselfie.ALBUM"

        @JvmStatic
        fun showAlbumDetails(host: AppCompatActivity, album: CompleteAlbum) =
                Intent(host, PhotoLoadingActivity::class.java).apply {
                    putExtra(ALBUM_KEY, album)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_loading)
        setSupportActionBar(toolbar)

        addPicture.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

}
