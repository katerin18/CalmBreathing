package com.example.calmingbreath

import retrofit2.http.Body
import retrofit2.http.POST

interface MeasurementsApi {
    @POST("api/measurements")
    suspend fun create(@Body request: MeasurementRequest): Unit
}