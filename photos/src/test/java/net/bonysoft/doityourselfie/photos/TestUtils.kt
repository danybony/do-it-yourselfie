package net.bonysoft.doityourselfie.photos

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import java.io.File

object TestUtils {

    private val okHttpClient = OkHttpClient()

    fun fetchGetAlbumsJson() = fetchJsonByCompletePath(GET_ALBUMS)

    fun fetchPostAlbumsJson() = fetchJsonByCompletePath(POST_ALBUMS)

    fun fetchListPhotosJson() = fetchJsonByCompletePath(LIST_PHOTOS)

    fun fetchListPhotos2Json() = fetchJsonByCompletePath(LIST_PHOTOS_2)

    private fun fetchJsonByCompletePath(filePath: String): String {
        val file = File(javaClass.classLoader!!.getResource(filePath).path)
        return String(file.readBytes())
    }

    fun okHttpClient() = okHttpClient

    fun moshi() = Moshi.Builder().build()

    fun mockWebServer(success: Boolean = true) =
            MockWebServer().apply {
                setDispatcher(PhotosDispatcher(success))
            }
}