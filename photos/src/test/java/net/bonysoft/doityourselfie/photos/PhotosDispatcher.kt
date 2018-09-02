package net.bonysoft.doityourselfie.photos

import net.bonysoft.doityourselfie.photos.TestUtils.fetchGetAlbumsJson
import net.bonysoft.doityourselfie.photos.TestUtils.fetchPostAlbumsJson
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class PhotosDispatcher(private val success: Boolean = true) : Dispatcher() {

    companion object {
        private const val POST = "POST"
        private const val ALBUMS = "/albums"
        private const val CREATE_MEDIA_LINK = "./mediaItems:batchCreate"
        private const val LIST_PHOTOS = "./mediaItems:search"
    }

    private val failResponse = MockResponse()
            .setResponseCode(400)
            .setBody("No permission to add media items to this album.")
            .setStatus("INVALID_ARGUMENT")

    override fun dispatch(request: RecordedRequest?): MockResponse =
            if (success) {
                dispatchSuccess(request)
            } else {
                dispatchFailure()
            }

    private fun dispatchSuccess(request: RecordedRequest?): MockResponse =
            when (request?.path) {
                ALBUMS -> responseWith(albumResponse(request))
                CREATE_MEDIA_LINK -> responseWith(TestUtils.fetchListPhotosJson())
                LIST_PHOTOS -> responseWith(TestUtils.fetchListPhotos2Json())
                else -> failResponse
            }

    private fun albumResponse(request: RecordedRequest) =
            if (request.method == POST) {
                fetchPostAlbumsJson()
            } else {
                fetchGetAlbumsJson()
            }

    private fun responseWith(json: String): MockResponse =
            MockResponse().setResponseCode(200).setBody(json)

    private fun dispatchFailure() = failResponse
}