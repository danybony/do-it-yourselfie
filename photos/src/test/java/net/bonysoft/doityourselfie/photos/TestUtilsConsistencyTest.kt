package net.bonysoft.doityourselfie.photos

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TestUtilsConsistencyTest {

    @Test
    fun get_albums_is_not_null() {
        assertThat(TestUtils.fetchGetAlbumsJson())
                .isNotNull()
                .isNotBlank()
                .isNotEmpty()
    }

    @Test
    fun post_albums_is_not_null() {
        assertThat(TestUtils.fetchPostAlbumsJson())
                .isNotNull()
                .isNotBlank()
                .isNotEmpty()
    }

    @Test
    fun list_photos_is_not_null() {
        assertThat(TestUtils.fetchListPhotosJson())
                .isNotNull()
                .isNotBlank()
                .isNotEmpty()
    }

    @Test
    fun list_photos_2_is_not_null() {
        assertThat(TestUtils.fetchListPhotos2Json())
                .isNotNull()
                .isNotBlank()
                .isNotEmpty()
    }

    @Test
    fun batch_create_is_not_null() {
        assertThat(TestUtils.fetchBatchCreateJson())
                .isNotNull()
                .isNotBlank()
                .isNotEmpty()
    }
}