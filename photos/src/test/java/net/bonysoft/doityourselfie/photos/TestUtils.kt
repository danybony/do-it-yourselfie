package net.bonysoft.doityourselfie.photos

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import java.io.File

object TestUtils {

    private const val BASE_PATH = "json/"
    private const val GET_ALBUMS = "${BASE_PATH}get_albums.json"
    private const val POST_ALBUMS = "${BASE_PATH}post_albums.json"
    private const val LIST_PHOTOS = "${BASE_PATH}list_photos.json"
    private const val LIST_PHOTOS_2 = "${BASE_PATH}list_photos_2.json"

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