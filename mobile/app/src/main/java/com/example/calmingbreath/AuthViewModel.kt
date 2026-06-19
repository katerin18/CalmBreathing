package com.example.calmingbreath

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthState(
    val firstName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val acceptedTerms: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
)

sealed interface AuthAction {
    data class FirstNameChanged(val value: String) : AuthAction
    data class EmailChanged(val value: String) : AuthAction
    data class PasswordChanged(val value: String) : AuthAction
    data class ConfirmPasswordChanged(val value: String) : AuthAction

    data object LoginClicked : AuthAction
    data object RegisterClicked : AuthAction
    data object LogoutClicked : AuthAction
    data object ForgotPasswordClicked : AuthAction

    data object NavigateToRegister : AuthAction
    data object NavigateToLogin : AuthAction
}

class AuthViewModel(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val userStore: UserStore,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        // Восстанавливаем профиль с прошлого сеанса (нужно для авто-входа, когда логина не было).
        userStore.getUser()?.let { saved ->
            _state.update { it.copy(user = saved) }
        }
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.FirstNameChanged ->
                _state.update { it.copy(firstName = action.value, errorMessage = null) }

            is AuthAction.EmailChanged ->
                _state.update { it.copy(email = action.value, errorMessage = null) }

            is AuthAction.PasswordChanged ->
                _state.update { it.copy(password = action.value, errorMessage = null) }

            is AuthAction.ConfirmPasswordChanged ->
                _state.update { it.copy(confirmPassword = action.value, errorMessage = null) }

            AuthAction.LoginClicked -> login()
            AuthAction.RegisterClicked -> register()
            AuthAction.LogoutClicked -> logout()

            AuthAction.ForgotPasswordClicked,
            AuthAction.NavigateToRegister,
            AuthAction.NavigateToLogin -> Unit
        }
    }

    private fun login() {
        val current = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val res = authApi.login(
                    LoginRequest(email = current.email, password = current.password)
                )
                Log.d("Auth", "login access = ${res.accessToken}")
                Log.d("Auth", "login refresh = ${res.refreshToken}")
                tokenManager.saveTokens(
                    AccessToken(res.accessToken),
                    RefreshToken(res.refreshToken)
                )
                userStore.saveUser(res.user)
                _state.update { it.copy(isLoading = false, isAuthenticated = true, user = res.user) }
            } catch (e: Exception) {
                Log.e("Auth", "login error = $e")
                _state.update {
                    it.copy(isLoading = false, errorMessage = "Не удалось войти. Проверьте данные.")
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            try {
                authApi.logout()
            } catch (e: Exception) {
                Log.e("Auth", "logout error = $e")
            }
            tokenManager.clearTokens()
            userStore.clear()
            _state.value = AuthState()
        }
    }

    private fun register() {
        val current = _state.value
        if (current.password != current.confirmPassword) {
            _state.update { it.copy(errorMessage = "Пароли не совпадают") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val res = authApi.register(
                    RegisterRequest(
                        email = current.email,
                        firstName = current.firstName,
                        lastName = null,
                        password = current.password,
                        confirmPassword = current.confirmPassword
                    )
                )
                Log.d("Auth", "register access = ${res.accessToken}")
                Log.d("Auth", "register refresh = ${res.refreshToken}")
                tokenManager.saveTokens(
                    AccessToken(res.accessToken),
                    RefreshToken(res.refreshToken)
                )
                userStore.saveUser(res.user)
                _state.update { it.copy(isLoading = false, isAuthenticated = true, user = res.user) }
            } catch (e: Exception) {
                Log.e("Auth", "register error = $e")
                _state.update {
                    it.copy(isLoading = false, errorMessage = "Не удалось зарегистрироваться.")
                }
            }
        }
    }
}
