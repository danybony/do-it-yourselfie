package net.bonysoft.doityourselfie.photos.internal

import kotlinx.coroutines.experimental.runBlocking
import net.bonysoft.doityourselfie.photos.ALBUM_008_ID
import net.bonysoft.doityourselfie.photos.ALBUM_008_TITLE
import net.bonysoft.doityourselfie.photos.ALBUM_008_URL
import net.bonysoft.doityourselfie.photos.TestUtils.mockWebServer
import net.bonysoft.doityourselfie.photos.TestUtils.moshi
import net.bonysoft.doityourselfie.photos.TestUtils.okHttpClient
import net.bonysoft.doityourselfie.photos.expectedAlbums
import net.bonysoft.doityourselfie.photos.network.UploadApiService
import net.bonysoft.doityourselfie.photos.network.createRetrofit
import net.bonysoft.doityourselfie.photos.utils.ImageTransformer
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class PhotosApiImplTest {

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
        val albumResponse = runBlocking {
            photosApi.createAlbum(ALBUM_008_TITLE).await()
        }

        with(albumResponse) {
            assertThat(title).isEqualToIgnoringCase(ALBUM_008_TITLE)
            assertThat(id).isEqualTo(ALBUM_008_ID)
            assertThat(productUrl).isEqualTo(ALBUM_008_URL)
            assertThat(writeable).isFalse()
        }
    }

    @Test
    fun albums_are_fetched_correctly() {
        val albumResponse = runBlocking {
            photosApi.fetchAlbums().await()
        }

        assertThat(albumResponse.size).isEqualTo(expectedAlbums.size)

        //Checks that expectedAlbums contains all the items contained in albumResponse
        val thisShouldBe0 = albumResponse.filterNot { expectedAlbums.contains(it) }.count()
        assertThat(thisShouldBe0).isEqualTo(0)

        //And vice-versa
        val thisAlsoShouldBe0 = expectedAlbums.filterNot { albumResponse.contains(it) }.count()
        assertThat(thisAlsoShouldBe0).isEqualTo(0)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}