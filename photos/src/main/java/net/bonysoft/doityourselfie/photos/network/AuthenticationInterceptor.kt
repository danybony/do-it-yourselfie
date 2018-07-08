package net.bonysoft.doityourselfie.photos.network

import okhttp3.Interceptor
import okhttp3.Response

internal class AuthenticationInterceptor(private val oAuth2Token: String) : Interceptor {

    companion object {
        private const val TOKEN = "Authorization"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return if (chain.request().headers("token").isEmpty()) {
            chain.proceed(chain.request())
        } else {
            chain.proceed(authenticateChain(chain))
        }
    }

    private fun authenticateChain(chain: Interceptor.Chain) =
            chain.request()
                    .newBuilder()
                    .removeHeader("token")
                    .addHeader(TOKEN, "Bearer $oAuth2Token")
                    .build()
}