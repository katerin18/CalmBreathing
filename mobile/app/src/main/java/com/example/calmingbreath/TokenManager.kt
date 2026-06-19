package com.example.calmingbreath

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

const val ACCESS_KEY = "access_key"
const val REFRESH_KEY = "refresh_key"

@JvmInline
value class AccessToken(val value: String)

@JvmInline
value class RefreshToken(val value: String)

class TokenManager(appContext: Context) {
    private val masterKey = MasterKey.Builder(appContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        appContext,
        "auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: AccessToken, refreshToken: RefreshToken) {
        prefs.edit()
            .putString(ACCESS_KEY, accessToken.value)
            .putString(REFRESH_KEY, refreshToken.value)
            .apply()
    }

    fun getAccessToken(): AccessToken? {
        return prefs.getString(ACCESS_KEY, null)?.let {
            AccessToken(it)
        }
    }

    fun getRefreshToken(): RefreshToken? {
        return prefs.getString(REFRESH_KEY, null)?.let {
            RefreshToken(it)
        }
    }

    fun updateAccessToken(newAccessToken: AccessToken) {
        prefs.edit()
            .putString(ACCESS_KEY, newAccessToken.value)
            .apply()
    }
    fun clearTokens() {
        prefs.edit().clear().apply()
    }
}