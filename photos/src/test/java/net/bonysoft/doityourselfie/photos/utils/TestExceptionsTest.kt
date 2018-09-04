package net.bonysoft.doityourselfie.photos.utils

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TestExceptionsTest {

    //String cleaning
    @Test
    fun null_string_returns_null() {
        val input: String? = null
        assertThat(input.clean()).isNull()
    }

    @Test
    fun string_with_parameters_is_cleared() {
        val input = "/mediaItems:search?albumId=&pageSize=100"
        val expectedOutput = "/mediaItems:search"

        assertThat(input.clean()).isEqualTo(expectedOutput)
    }

    @Test
    fun string_without_parameters_is_not_cleared() {
        val input = "/mediaItems:search"
        val expectedOutput = "/mediaItems:search"

        assertThat(input.clean()).isEqualTo(expectedOutput)
    }
}