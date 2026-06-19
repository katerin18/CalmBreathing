package com.example.calmingbreath

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_URL = "http://10.0.2.2:8080/"

object RetrofitLogic {
    // Аутентифицированный Retrofit: интерсептор добавляет Bearer, Authenticator обновляет токен по 401.
    private fun buildAuthedRetrofit(tokenManager: TokenManager): Retrofit {
        val gson = GsonBuilder().create()

        // 1) ЧИСТЫЙ retrofit — без интерсептора и Authenticator, только для refresh
        val plainClient = OkHttpClient.Builder().build()
        val refreshApi = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(plainClient)
            .build()
            .create(AuthApi::class.java)

        // 2) ОСНОВНОЙ client — с интерсептором И Authenticator (он получает refreshApi)
        val mainClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .authenticator(TokenAuthenticator(tokenManager, refreshApi))
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(mainClient)
            .build()
    }

    fun createAuthApi(tokenManager: TokenManager): AuthApi =
        buildAuthedRetrofit(tokenManager).create(AuthApi::class.java)

    fun createMeasurementsApi(tokenManager: TokenManager): MeasurementsApi =
        buildAuthedRetrofit(tokenManager).create(MeasurementsApi::class.java)
}