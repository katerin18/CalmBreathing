package com.example.calmingbreath

data class LoginRequest(
    val email: String,
    val password: String
)

data class RefreshRequest(
    val refreshToken: String
)

data class RegisterRequest(
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val password: String,
    val confirmPassword: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val user: User
)

data class TokenResponse(
    val accessToken: String,
    val tokenType: String,
    val expiresIn: Long
)

data class User(
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val enabled: Boolean,
    val roles: List<String>
)

data class MeasurementRequest(
    val startPulse: Int,
    val exerciseDurationSeconds: Long,
    val endPulse: Int,
    val measuredAt: String, // ISO local date-time, напр. "2026-05-14T10:30:00"
)


