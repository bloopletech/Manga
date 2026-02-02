package net.bloople.manga

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response
import kotlin.text.isNotEmpty

class HttpAuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val username = request.url.username
        val password = request.url.password
        if(username.isNotEmpty() && password.isNotEmpty()) {
            val authenticatedRequest = request
                .newBuilder()
                .header("Authorization", Credentials.basic(username, password))
                .build()
            return chain.proceed(authenticatedRequest)
        }
        return chain.proceed(request)
    }
}