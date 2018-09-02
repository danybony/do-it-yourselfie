package net.bonysoft.doityourselfie.photos.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class ExtensionsTest {

    @Test
    fun string_is_converted_into_album() {
        val expectedTitle = "Test title"
        val computedAlbum = expectedTitle.toAlbumRequest()

        assertThat(computedAlbum.album.title).isEqualTo(expectedTitle)
    }

    @Test
    fun string_is_converted_into_upload_request() {
        val expectedId = "expected_id"
        val expectedDescription = "expected description"
        val targetString = "target string"

        val computedRequest = targetString.asImageUploadRequestWith(expectedId, expectedDescription)

        assertThat(computedRequest.albumId).isEqualTo(expectedId)
        assertThat(computedRequest.newMediaItems.size).isEqualTo(1)

        val item = computedRequest.newMediaItems.firstOrNull()

        assertThat(item).isNotNull
        assertThat(item!!.description).isEqualTo(expectedDescription)
        assertThat(item.simpleMediaItem.uploadToken).isEqualTo(targetString)
    }

    //Next page token
    @Test
    fun when_both_receiver_and_token_are_null_it_should_return_null() {
        val receiver: String? = null
        assertThat(receiver.replaceWith(null)).isNull()
    }

    @Test
    fun when_receiver_is_not_null_but_token_is_it_should_return_null() {
        val receiver = "receiver"
        assertThat(receiver.replaceWith(null)).isNull()
    }

    @Test
    fun when_receiver_is_different_from_token_it_should_return_token() {
        val receiver = "receiver"
        val token = "token"
        assertThat(receiver.replaceWith(token)).isEqualTo(token)
    }

    @Test
    fun when_receiver_is_the_same_than_token_it_should_return_null() {
        val receiver = "receiver"
        val token = "receiver"
        assertThat(receiver.replaceWith(token)).isNull()
    }

    @Test
    fun when_receiver_is_null_but_token_is_not_it_should_return_token() {
        val receiver: String? = null
        val token = "token"
        assertThat(receiver.replaceWith(token)).isEqualTo(token)
    }
}