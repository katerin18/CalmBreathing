package com.example.calmingbreath

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface MeasurementsApi {
    @POST("api/measurements")
    suspend fun create(@Body request: MeasurementRequest): Unit

    // Возвращает только замеры текущего пользователя (фильтрация по токену на сервере).
    @GET("api/measurements")
    suspend fun list(): List<MeasurementResponse>
}