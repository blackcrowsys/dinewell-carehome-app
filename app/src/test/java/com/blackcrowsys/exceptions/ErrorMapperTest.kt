package com.blackcrowsys.exceptions

import com.blackcrowsys.R
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import retrofit2.Response
import java.net.HttpURLConnection

/**
 * Unit test for [ErrorMapper].
 */
@RunWith(RobolectricTestRunner::class)
class ErrorMapperTest {

    private lateinit var errorMapper: ErrorMapper

    @Before
    fun setUp() {
        errorMapper = ErrorMapper(RuntimeEnvironment.application)
    }

    @Test
    fun `transformException when InvalidUrlException`() {
        val transformException = errorMapper.transformException(InvalidUrlException())

        assertTrue(transformException.message == RuntimeEnvironment.application.getString(R.string.url_invalid_error))
    }

    @Test
    fun `transformException when EmptyUsernamePasswordException`() {
        val transformException = errorMapper.transformException(EmptyUsernamePasswordException())

        assertTrue(transformException.message == RuntimeEnvironment.application.getString(R.string.empty_username_password_error))
    }

    @Test
    fun `transformException when 401 HttpException`() {
        val transformException = errorMapper.transformException(HttpException(Response.error<String>(
            HttpURLConnection.HTTP_UNAUTHORIZED, ResponseBody.create(MediaType.parse("application/json"), ""))))

        assertTrue(transformException.message == RuntimeEnvironment.application.getString(R.string.incorrect_login_details))
    }

    @Test
    fun `transformException when generic error`() {
        val transformException = errorMapper.transformException(Exception())

        assertTrue(transformException.message == RuntimeEnvironment.application.getString(R.string.unknown_error))
    }

    @Test
    fun `transformException when PinContainsSameCharactersException`() {
        val transformException =
            errorMapper.transformException(PinContainsSameCharactersException())

        assertTrue(transformException.message == RuntimeEnvironment.application.getString(R.string.pin_contains_same_characters_error))
    }

    @Test
    fun `transformException when ConfirmedPinDoesNotMatchException`() {
        val transformException = errorMapper.transformException(ConfirmedPinDoesNotMatchException())

        assertTrue(transformException.message == RuntimeEnvironment.application.getString(R.string.confirmed_pin_does_not_match_error))
    }
}