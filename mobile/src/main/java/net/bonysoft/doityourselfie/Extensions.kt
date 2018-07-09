package net.bonysoft.doityourselfie

import android.content.res.Resources
import android.support.annotation.IdRes
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.util.TypedValue.applyDimension
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import net.bonysoft.doityourselfie.photos.model.CompleteAlbum

fun View.textView(@IdRes id: Int): TextView = findViewById(id)

fun View.imageView(@IdRes id: Int): ImageView = findViewById(id)

fun Int.toPixel(resources: Resources) : Int =
        applyDimension(COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics).toInt()

fun CompleteAlbum.fetchImageOfSize(pixel: Int) : String =
        "$coverPhotoBaseUrl=w$pixel-h$pixel"