package net.bonysoft.doityourselfie.photos.views

import android.graphics.Bitmap

interface PhotoLoadingView {

    fun onLoading(fileName: String, bitmap: Bitmap)

    fun onComplete()

    fun onError(throwable: Throwable)
}