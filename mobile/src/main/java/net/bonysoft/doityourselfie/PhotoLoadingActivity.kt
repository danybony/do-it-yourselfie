package net.bonysoft.doityourselfie

import `in`.mayanknagwanshi.imagepicker.imagePicker.ImagePicker
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.design.widget.Snackbar.LENGTH_INDEFINITE
import android.support.design.widget.Snackbar.make
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat.checkSelfPermission
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_photo_loading.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import net.bonysoft.doityourselfie.photos.PhotosAPI
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum
import net.bonysoft.doityourselfie.photos.model.NewMediaItemResult
import net.bonysoft.doityourselfie.photos.views.PhotoLoadingView
import timber.log.Timber

class PhotoLoadingActivity : AppCompatActivity(), PhotoLoadingView {

    companion object {
        private const val ALBUM_KEY = "net.bonysoft.doityourselfie.ALBUM"
        private const val STORAGE_REQUEST_CODE = 1337

        @JvmStatic
        fun showAlbumDetails(host: AppCompatActivity,
                             album: CompleteAlbum) {
            host.startActivity(
                    Intent(host, PhotoLoadingActivity::class.java).apply {
                        putExtra(ALBUM_KEY, album)
                    }
            )
        }

        @JvmStatic
        fun Intent.album() = getParcelableExtra<CompleteAlbum>(ALBUM_KEY)
    }

    private val picker = ImagePicker()
    private lateinit var photosAPI: PhotosAPI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_loading)
        setSupportActionBar(toolbar)

        addPicture.setOnClickListener {
            loadPicturePicker()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        photosAPI = PhotosAPI(application, token(), BuildConfig.DEBUG)
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
            onLoading(path.extractName(), bitmap)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onLoading(fileName: String, bitmap: Bitmap) {
        imagePreview.setImageBitmap(bitmap)
        loadingUi.show()
        photoList.hide()
        addPicture.hide()

        launch(UI) {
            try {
                val results = photosAPI.uploadImage(intent.album(), fileName, bitmap).await()
                val errors = results.newMediaItemResults.filter { it.status.code != 0 }

                if (errors.isNotEmpty()) {
                    onSoftError(errors)
                } else {
                    onComplete()
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    override fun onComplete() {
        loadingUi.hide()
        photoList.show()
        addPicture.show()
        Toast.makeText(this, "OK", Toast.LENGTH_LONG).show()
    }

    private fun onSoftError(errors: List<NewMediaItemResult>) {
        loadingUi.hide()
        photoList.show()
        addPicture.show()

        val message = "${errors.size} errors." +
                "${errors.mapIndexed { index, error -> "\n$index: ${error.status.code} - ${error.status.message}" }}"

        Timber.d(message)

        AlertDialog.Builder(this)
                .setTitle("Content error")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
                .show()
    }

    override fun onError(throwable: Throwable) {
        loadingUi.hide()
        photoList.show()
        addPicture.show()
        Timber.e(throwable)
        make(photoList, "Error: ${throwable.message}", LENGTH_INDEFINITE)
                .setAction("DISMISS") { }
                .show()
    }
}
