package net.bonysoft.doityourselfie.photos

import java.io.File

object TestUtils {

    private const val BASE_PATH = "json/"
    private const val GET_ALBUMS = "${BASE_PATH}get_albums.json"
    private const val POST_ALBUMS = "${BASE_PATH}post_albums.json"
    private const val LIST_PHOTOS = "${BASE_PATH}list_photos.json"
    private const val LIST_PHOTOS_2 = "${BASE_PATH}list_photos_2.json"

    fun fetchGetAlbumsJson() = fetchJsonByCompletePath(GET_ALBUMS)

    fun fetchPostAlbumsJson() = fetchJsonByCompletePath(POST_ALBUMS)

    fun fetchListPhotosJson() = fetchJsonByCompletePath(LIST_PHOTOS)

    fun fetchListPhotos2Json() = fetchJsonByCompletePath(LIST_PHOTOS_2)

    private fun fetchJsonByCompletePath(filePath: String): String {
        val file = File(javaClass.classLoader!!.getResource(filePath).path)
        return String(file.readBytes())
    }
}