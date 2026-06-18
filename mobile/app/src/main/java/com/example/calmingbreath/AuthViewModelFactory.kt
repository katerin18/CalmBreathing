package com.example.calmingbreath

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AuthViewModelFactory(private val context: Context): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val tokenManager = TokenManager(context)
        val authApi = RetrofitLogic.createAuthApi(tokenManager)
        val userStore = UserStore(context)

        return AuthViewModel(authApi, tokenManager, userStore) as T
    }
}