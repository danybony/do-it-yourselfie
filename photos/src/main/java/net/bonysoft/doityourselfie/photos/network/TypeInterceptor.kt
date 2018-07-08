package net.bonysoft.doityourselfie.photos.network

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

internal class TypeIncerceptor : Interceptor {

    companion object {
        private const val TYPE = "Content-type"
        private const val JSON = "application/json"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = when {
            chain.request().headers("json").isNotEmpty() -> setJsonContentType(chain)
            else -> chain.request()
        }

        return chain.proceed(request)
    }

    private fun setJsonContentType(chain: Interceptor.Chain): Request =
            chain.request()
                    .newBuilder()
                    .removeHeader("json")
                    .addHeader(TYPE, JSON)
                    .build()

}