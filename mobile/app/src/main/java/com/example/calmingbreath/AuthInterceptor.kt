package com.example.calmingbreath

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: TokenManager): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        val publicPaths = listOf("/api/auth/login", "/api/auth/register", "/api/auth/refresh")

        return if (publicPaths.any { path.contains(it) }) {
            chain.proceed(request)
        } else {
            val newRequest = tokenManager.getAccessToken()?.let {
                request.newBuilder()
                    .header("Authorization", "Bearer ${it.value}")
                    .build()
            }
            chain.proceed(newRequest ?: request)
        }
    }
}