package net.bonysoft.doityourselfie

import android.content.Intent
import android.content.res.Resources
import android.support.annotation.IdRes
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_main.*
import net.bonysoft.doityourselfie.photos.model.AlbumResponse
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum
import net.bonysoft.doityourselfie.photos.model.MediaItem
import net.bonysoft.doityourselfie.standalone.StandAloneAuthenticationActivity

fun View.textView(@IdRes id: Int): TextView = findViewById(id)

fun View.imageView(@IdRes id: Int): ImageView = findViewById(id)

fun Int.toPixel(resources: Resources): Int =
        applyDimension(COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics).toInt()

fun CompleteAlbum.fetchImageOfSize(pixel: Int): String =
        "$coverPhotoBaseUrl=w$pixel-h$pixel"

fun MediaItem.fetchImageOfSize(pixel: Int): String =
        "$baseUrl=w$pixel-h$pixel"

fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)

fun View.lazyTextView(@IdRes id: Int): Lazy<TextView> = unsafeLazy { findViewById<TextView>(id) }

fun View.lazyView(@IdRes id: Int): Lazy<View> = unsafeLazy { findViewById<View>(id) }

fun AlbumResponse.toCompleteAlbum() =
        CompleteAlbum(
                id = this.id,
                title = this.title,
                productUrl = this.productUrl,
                coverPhotoBaseUrl = "",
                isWriteable = this.writeable,
                totalMediaItems = 0
        )

val EMPTY_ALBUM_RESPONSE = AlbumResponse("", "", "", false)

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun String.extractName() = this.split("/").last()

const val TOKEN_KEY = "net.bonysoft.doityourselfie.TOKEN"

fun token() = Hawk.get<String>(TOKEN_KEY)!!

fun MainActivity.setStandAloneAuthentication() {
    if (BuildConfig.IS_STANDALONE) {
        standAloneAuthentication.run {
            visibility = View.VISIBLE
            setOnClickListener {
                startActivity(Intent(this@setStandAloneAuthentication, StandAloneAuthenticationActivity::class.java))
            }
        }
    } else {
        standAloneAuthentication.visibility = View.GONE
    }
}