package com.example.calmingbreath

import android.content.Context
import com.google.gson.Gson

const val USER_KEY = "user_json"

class UserStore(appContext: Context) {
    private val prefs = appContext.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: User) {
        prefs.edit().putString(USER_KEY, gson.toJson(user)).apply()
    }

    fun getUser(): User? {
        val json = prefs.getString(USER_KEY, null) ?: return null
        return try {
            gson.fromJson(json, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}