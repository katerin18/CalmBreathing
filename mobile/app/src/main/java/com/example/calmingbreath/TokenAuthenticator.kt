package com.example.calmingbreath

import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: TokenManager,
    private val refreshApi: AuthApi
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val refreshToken = tokenManager.getRefreshToken() ?: return null

        val newAccessTokenResponse = try {
            runBlocking {
                refreshApi.refresh(RefreshRequest(refreshToken.value))
            }
        } catch (e: Exception) {
            return null
        }
        tokenManager.updateAccessToken(AccessToken(newAccessTokenResponse.accessToken))

        return response.request
            .newBuilder()
            .header("Authorization", "Bearer ${newAccessTokenResponse.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}