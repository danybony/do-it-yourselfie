package net.bonysoft.doityourselfie.photos.internal

import android.telephony.PhoneNumberFormattingTextWatcher
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.experimental.runBlocking
import net.bonysoft.doityourselfie.photos.TestUtils.mockWebServer
import net.bonysoft.doityourselfie.photos.TestUtils.moshi
import net.bonysoft.doityourselfie.photos.TestUtils.okHttpClient
import net.bonysoft.doityourselfie.photos.model.Album
import net.bonysoft.doityourselfie.photos.model.AlbumRequest
import net.bonysoft.doityourselfie.photos.network.UploadApiService
import net.bonysoft.doityourselfie.photos.network.createRetrofit
import net.bonysoft.doityourselfie.photos.utils.ImageTransformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class PhotosApiImplTest {

    companion object {
        private const val ALBUM_ID = "AGj1epU_sPGiLToxukVK9clQYxZf3h1N6eqjGyq90gzv9vmK3CTU"
        private const val ALBUM_TITLE = "Test Album 008"
        private const val ALBUM_URL =
                "https://photos.google.com/lr/album/AGj1epU_sPGiLToxukVK9clQYxZf3h1N6eqjGyq90gzv9vmK3CTU"
    }

    private val mockWebServer = mockWebServer()
    private val imageTransformer = ImageTransformer()
    private lateinit var photosApi: PhotosApiImpl

    @Before
    fun setUp() {
        val baseUrl = mockWebServer.url("/").toString()
        val apiService = createRetrofit(moshi(), okHttpClient(), baseUrl)
        val uploadService = UploadApiService(okHttpClient(), baseUrl)
        photosApi = PhotosApiImpl(apiService, uploadService, imageTransformer, "")
    }

    @Test
    fun album_is_created_correctly() {
        runBlocking {
            val albumResponse = photosApi.createAlbum(ALBUM_TITLE).await()
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