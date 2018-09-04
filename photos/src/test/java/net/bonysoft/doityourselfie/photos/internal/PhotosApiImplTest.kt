package net.bonysoft.doityourselfie.photos.internal

import kotlinx.coroutines.experimental.runBlocking
import net.bonysoft.doityourselfie.photos.*
import net.bonysoft.doityourselfie.photos.TestUtils.mockWebServer
import net.bonysoft.doityourselfie.photos.TestUtils.moshi
import net.bonysoft.doityourselfie.photos.TestUtils.okHttpClient
import net.bonysoft.doityourselfie.photos.network.UploadApiService
import net.bonysoft.doityourselfie.photos.network.createRetrofit
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class PhotosApiImplTest {

    private val mockWebServer = mockWebServer()
    private lateinit var photosApi: PhotosApiImpl

    @Before
    fun setUp() {
        val baseUrl = mockWebServer.url("/").toString()
        val apiService = createRetrofit(moshi(), okHttpClient(), baseUrl)
        val uploadService = UploadApiService(okHttpClient(), baseUrl)
        photosApi = PhotosApiImpl(apiService, uploadService, "")
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

    @Test
    fun photo_is_uploaded_correctly() {
        val photoResponse = runBlocking {
            photosApi.uploadImage(PHOTO_ALBUM_ID, PHOTO_NAME, ByteArray(0)).await()
        }

        assertThat(photoResponse.newMediaItemResults.size).isEqualTo(1)

        val item = photoResponse.newMediaItemResults.firstOrNull()
        assertThat(item).isNotNull
        assertThat(item!!.uploadToken).isEqualTo(UPLOAD_TOKEN)
        assertThat(item.status.message).isEqualTo("OK")
        assertThat(item.mediaItem.id).isEqualTo(PHOTO_ALBUM_ID)
        assertThat(item.mediaItem.description).isNotNull().isEqualTo(PHOTO_NAME)
    }

    @Test
    fun photo_list_is_fetched_correctly() {
        val photoList = runBlocking {
            photosApi.fetchPicturesInAlbum("").await()
        }

        assertThat(photoList.size).isEqualTo(expectedPhotos.size)

        //Checks that expectedPhotos contains all the items contained in photoList
        val photoListTitles = photoList.map { it.description }
        val expectedPhotosTitle = expectedPhotos.map { it.description }

        assertThat(
                photoListTitles.filterNot { expectedPhotosTitle.contains(it) }.count()
        ).isEqualTo(0)

        assertThat(
                expectedPhotosTitle.filterNot { photoListTitles.contains(it) }.count()
        ).isEqualTo(0)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }
}