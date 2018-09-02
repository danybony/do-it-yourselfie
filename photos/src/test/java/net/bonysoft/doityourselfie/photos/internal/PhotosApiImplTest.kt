package net.bonysoft.doityourselfie.photos.internal

import kotlinx.coroutines.experimental.runBlocking
import net.bonysoft.doityourselfie.photos.TestUtils.mockWebServer
import net.bonysoft.doityourselfie.photos.TestUtils.moshi
import net.bonysoft.doityourselfie.photos.TestUtils.okHttpClient
import net.bonysoft.doityourselfie.photos.model.Album
import net.bonysoft.doityourselfie.photos.model.AlbumRequest
import net.bonysoft.doityourselfie.photos.network.createRetrofit
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Test

class PhotosApiImplTest {

    companion object {
        private const val ALBUM_ID = "AGj1epU_sPGiLToxukVK9clQYxZf3h1N6eqjGyq90gzv9vmK3CTU"
        private const val ALBUM_TITLE = "Test Album 008"
        private const val ALBUM_URL =
                "https://photos.google.com/lr/album/AGj1epU_sPGiLToxukVK9clQYxZf3h1N6eqjGyq90gzv9vmK3CTU"
    }

    private val mockWebServer = mockWebServer()
    private val testedService = createRetrofit(moshi(), okHttpClient(), mockWebServer.url("/").toString())
    private val albumRequest = AlbumRequest(Album(ALBUM_TITLE))

    @Test
    fun album_is_created_correctly() {
        runBlocking {
            val albumResponse = testedService.createAlbum("", albumRequest).await()
            with(albumResponse) {
                assertThat(title).isEqualToIgnoringCase(ALBUM_TITLE)
                assertThat(id).isEqualTo(ALBUM_ID)
                assertThat(productUrl).isEqualTo(ALBUM_URL)
                assertThat(writeable).isFalse()
            }
        }
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}