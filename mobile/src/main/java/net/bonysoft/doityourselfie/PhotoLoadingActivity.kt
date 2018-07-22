package net.bonysoft.doityourselfie

import `in`.mayanknagwanshi.imagepicker.imagePicker.ImagePicker
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_photo_loading.*
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum

class PhotoLoadingActivity : AppCompatActivity() {

    companion object {
        private const val ALBUM_KEY = "net.bonysoft.doityourselfie.ALBUM"
        private const val STORAGE_REQUEST_CODE = 1337

        @JvmStatic
        fun showAlbumDetails(host: AppCompatActivity, album: CompleteAlbum) {
            host.startActivity(
                    Intent(host, PhotoLoadingActivity::class.java).apply {
                        putExtra(ALBUM_KEY, album)
                    }
            )
        }
    }

    private val picker = ImagePicker()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_loading)
        setSupportActionBar(toolbar)

        addPicture.setOnClickListener {
            loadPicturePicker()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadPicturePicker() {
        if (checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PERMISSION_GRANTED) {
            startPicker()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(READ_EXTERNAL_STORAGE), STORAGE_REQUEST_CODE)
        }
    }

    private fun startPicker() {
        picker.withActivity(this)
                .chooseFromGallery(true)
                .chooseFromCamera(false)
                .withCompression(false)
                .start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == STORAGE_REQUEST_CODE) {
            checkPermissions(permissions, grantResults)
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun checkPermissions(permissions: Array<out String>, grantResults: IntArray) {
        permissions.forEachIndexed { index, permission ->
            if (permission == READ_EXTERNAL_STORAGE && grantResults[index] == PERMISSION_GRANTED) {
                startPicker()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ImagePicker.SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            val path = picker.getImageFilePath(data)
            val bitmap = BitmapFactory.decodeFile(path)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
