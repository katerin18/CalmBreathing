# Интеграция мобильного приложения с Backend

Полное руководство по интеграции iOS и Android приложений с REST API сервера.

## 📱 Требования

- iOS 13.0+ / Android 8.0+
- HTTP клиент (Alamofire для iOS, Retrofit для Android)
- Защищенное хранилище для токенов

## 🎯 Основной поток

```
┌─────────────────────────────────────────────────────────┐
│                  Мобильное приложение                   │
│  ┌──────────────────────────────────────────────────┐  │
│  │              Authentication Manager              │  │
│  │  • Управление токенами                          │  │
│  │  • Сохранение/восстановление учетных данных    │  │
│  │  • Автообновление токенов                       │  │
│  └────────────────────┬─────────────────────────────┘  │
│                       │                                 │
│  ┌────────────────────▼──────────────────────────────┐ │
│  │              API Request Manager                  │ │
│  │  • Добавление Authorization заголовка            │ │
│  │  • Перехват ошибок 401                           │ │
│  │  • Retry логика                                  │ │
│  └────────────────────┬──────────────────────────────┘ │
│                       │                                 │
│  ┌────────────────────▼──────────────────────────────┐ │
│  │              HTTP Client Layer                    │ │
│  │  • Alamofire (iOS) / Retrofit (Android)          │ │
│  └────────────────────┬──────────────────────────────┘ │
└────────────────────────┼──────────────────────────────────┘
                         │
          ┌──────────────▼──────────────┐
          │    REST API Server          │
          │  • JWT валидация            │
          │  • Обработка запросов       │
          └────────────────────────────┘
```

## 🔑 Управление токенами

### Сохранение токенов

#### Kotlin (Android)

```kotlin
class TokenManager(context: Context) {
    private val encryptedPreferences = EncryptedSharedPreferences.create(
        context,
        "auth_tokens",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String) {
        encryptedPreferences.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            putLong("token_timestamp", System.currentTimeMillis())
            apply()
        }
    }

    fun getAccessToken(): String? {
        return encryptedPreferences.getString("access_token", null)
    }

    fun getRefreshToken(): String? {
        return encryptedPreferences.getString("refresh_token", null)
    }

    fun clearTokens() {
        encryptedPreferences.edit().clear().apply()
    }

    fun isTokenExpired(): Boolean {
        val timestamp = encryptedPreferences.getLong("token_timestamp", 0)
        return System.currentTimeMillis() - timestamp > 3600000 // 1 час
    }
}
```

#### Swift (iOS)

```swift
import Security

class KeychainService {
    static let service = "com.calmbreathing.keychain"

    static func save(key: String, value: String) throws {
        let data = value.data(using: .utf8)!
        
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key,
            kSecValueData as String: data
        ]
        
        SecItemDelete(query as CFDictionary)
        let status = SecItemAdd(query as CFDictionary, nil)
        
        guard status == errSecSuccess else {
            throw KeychainError.saveFailed(status)
        }
    }

    static func load(key: String) throws -> String? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key,
            kSecReturnData as String: true
        ]
        
        var result: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &result)
        
        guard status == errSecSuccess else {
            if status == errSecItemNotFound {
                return nil
            }
            throw KeychainError.loadFailed(status)
        }
        
        guard let data = result as? Data else { return nil }
        return String(data: data, encoding: .utf8)
    }

    static func delete(key: String) throws {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: key
        ]
        
        let status = SecItemDelete(query as CFDictionary)
        guard status == errSecSuccess || status == errSecItemNotFound else {
            throw KeychainError.deleteFailed(status)
        }
    }
}
```

## 🌐 HTTP Клиент

### Kotlin + Retrofit

```kotlin
// Models
data class LoginRequest(
    val email: String,
    val password: String
)

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Long,
    val user: UserResponse
)

data class UserResponse(
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val enabled: Boolean,
    val roles: Set<String>
)

// API Service
interface ApiService {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("/api/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): TokenResponse

    @GET("/api/user/me")
    suspend fun getCurrentUser(): UserResponse

    @PUT("/api/user/me")
    suspend fun updateProfile(
        @Query("firstName") firstName: String?,
        @Query("lastName") lastName: String?
    ): UserResponse
}

// Retrofit Builder
class ApiClient(private val context: Context) {
    private val tokenManager = TokenManager(context)

    fun buildRetrofit(): Retrofit {
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor())
            .build()

        return Retrofit.Builder()
            .baseUrl("http://localhost:8080")
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

// Request Interceptor
class AuthInterceptor(private val tokenManager: TokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Пропустить публичные endpoints
        if (shouldSkipAuth(originalRequest.url.encodedPath)) {
            return chain.proceed(originalRequest)
        }

        // Добавить токен
        val accessToken = tokenManager.getAccessToken()
        val requestWithToken = if (accessToken != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $accessToken")
                .build()
        } else {
            originalRequest
        }

        var response = chain.proceed(requestWithToken)

        // Обработка 401
        if (response.code == 401) {
            response.close()
            
            val refreshToken = tokenManager.getRefreshToken()
            if (refreshToken != null) {
                val newAccessToken = refreshAccessToken(refreshToken)
                
                if (newAccessToken != null) {
                    tokenManager.saveTokens(newAccessToken, refreshToken)
                    
                    val newRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $newAccessToken")
                        .build()
                    
                    response = chain.proceed(newRequest)
                } else {
                    // Refresh failed - logout
                    tokenManager.clearTokens()
                }
            } else {
                // No refresh token - logout
                tokenManager.clearTokens()
            }
        }

        return response
    }

    private fun shouldSkipAuth(path: String): Boolean {
        val publicPaths = listOf(
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/refresh",
            "/actuator/health"
        )
        return publicPaths.any { path.contains(it) }
    }

    private fun refreshAccessToken(refreshToken: String): String? {
        return try {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("http://localhost:8080/api/auth/refresh")
                .post(RequestBody.create(
                    "application/json".toMediaType(),
                    "{\"refreshToken\":\"$refreshToken\"}"
                ))
                .build()

            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                json.getString("accessToken")
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
```

### Swift + Alamofire

```swift
import Alamofire
import Combine

// Models
struct LoginRequest: Codable {
    let email: String
    let password: String
}

struct AuthResponse: Codable {
    let accessToken: String
    let refreshToken: String
    let tokenType: String
    let expiresIn: Int64
    let user: UserResponse
}

struct TokenResponse: Codable {
    let accessToken: String
    let tokenType: String
    let expiresIn: Int64
}

struct UserResponse: Codable {
    let id: String
    let email: String
    let firstName: String?
    let lastName: String?
    let enabled: Bool
    let roles: [String]
}

// API Service
class ApiService {
    static let shared = ApiService()
    
    private let baseURL = "http://localhost:8080"
    private let tokenManager = TokenManager.shared
    
    // MARK: - Authentication
    
    func login(email: String, password: String) -> AnyPublisher<AuthResponse, AFError> {
        let request = LoginRequest(email: email, password: password)
        
        return AF.request(
            "\(baseURL)/api/auth/login",
            method: .post,
            parameters: request,
            encoder: JSONParameterEncoder.default
        )
        .responseDecodable(of: AuthResponse.self)
        .publishData()
        .compactMap { $0.value }
        .eraseToAnyPublisher()
    }
    
    func register(
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ) -> AnyPublisher<AuthResponse, AFError> {
        let request = RegisterRequest(
            email: email,
            password: password,
            confirmPassword: password,
            firstName: firstName,
            lastName: lastName
        )
        
        return AF.request(
            "\(baseURL)/api/auth/register",
            method: .post,
            parameters: request,
            encoder: JSONParameterEncoder.default
        )
        .responseDecodable(of: AuthResponse.self)
        .publishData()
        .compactMap { $0.value }
        .eraseToAnyPublisher()
    }
    
    // MARK: - User
    
    func getCurrentUser() -> AnyPublisher<UserResponse, AFError> {
        return authenticatedRequest("\(baseURL)/api/user/me", method: .get)
    }
    
    func updateProfile(
        firstName: String?,
        lastName: String?
    ) -> AnyPublisher<UserResponse, AFError> {
        let parameters: [String: String?] = [
            "firstName": firstName,
            "lastName": lastName
        ]
        
        return authenticatedRequest(
            "\(baseURL)/api/user/me",
            method: .put,
            parameters: parameters.compactMapValues { $0 }
        )
    }
    
    // MARK: - Private Methods
    
    private func authenticatedRequest<T: Decodable>(
        _ url: URLConvertible,
        method: HTTPMethod = .get,
        parameters: [String: Any]? = nil
    ) -> AnyPublisher<T, AFError> {
        guard let accessToken = tokenManager.getAccessToken() else {
            return Fail(error: AFError.invalidURL(url: url)).eraseToAnyPublisher()
        }
        
        var headers = HTTPHeaders()
        headers.add(.authorization(bearerToken: accessToken))
        
        return AF.request(
            url,
            method: method,
            parameters: parameters,
            headers: headers
        )
        .responseDecodable(of: T.self)
        .publishData()
        .compactMap { $0.value }
        .catch { error -> AnyPublisher<T, AFError> in
            // Обработка 401 ошибки
            if error.responseCode == 401 {
                return self.handleTokenRefresh()
                    .flatMap { _ in
                        self.authenticatedRequest(url, method: method, parameters: parameters)
                    }
                    .eraseToAnyPublisher()
            }
            return Fail(error: error).eraseToAnyPublisher()
        }
        .eraseToAnyPublisher()
    }
    
    private func handleTokenRefresh() -> AnyPublisher<Void, AFError> {
        guard let refreshToken = tokenManager.getRefreshToken() else {
            return Fail(error: AFError.invalidURL(url: URL(string: "")!)).eraseToAnyPublisher()
        }
        
        let request = RefreshTokenRequest(refreshToken: refreshToken)
        
        return AF.request(
            "\(baseURL)/api/auth/refresh",
            method: .post,
            parameters: request,
            encoder: JSONParameterEncoder.default
        )
        .responseDecodable(of: TokenResponse.self)
        .publishData()
        .handleEvents(receiveOutput: { response in
            self.tokenManager.saveAccessToken(response.accessToken)
        })
        .map { _ in () }
        .eraseToAnyPublisher()
    }
}
```

## 🔄 Автообновление токенов

### Kotlin Implementation

```kotlin
class AuthRepository(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(email: String, password: String): Result<AuthResponse> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(email, password))
            tokenManager.saveTokens(response.accessToken, response.refreshToken)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun refreshTokenIfNeeded(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (tokenManager.isTokenExpired() && tokenManager.getRefreshToken() != null) {
                val refreshToken = tokenManager.getRefreshToken()!!
                val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))
                tokenManager.saveTokens(response.accessToken, refreshToken)
            }
            Result.success(true)
        } catch (e: Exception) {
            tokenManager.clearTokens()
            Result.failure(e)
        }
    }
}
```

### Swift Implementation

```swift
class AuthManager: ObservableObject {
    @Published var isAuthenticated = false
    @Published var user: UserResponse?
    
    private let tokenManager = TokenManager.shared
    private var cancellables = Set<AnyCancellable>()
    
    func login(email: String, password: String) {
        ApiService.shared.login(email: email, password: password)
            .sink(
                receiveCompletion: { _ in },
                receiveValue: { [weak self] response in
                    self?.tokenManager.saveTokens(
                        accessToken: response.accessToken,
                        refreshToken: response.refreshToken
                    )
                    self?.user = response.user
                    self?.isAuthenticated = true
                }
            )
            .store(in: &cancellables)
    }
    
    func logout() {
        tokenManager.clearTokens()
        user = nil
        isAuthenticated = false
    }
    
    func getCurrentUser() {
        ApiService.shared.getCurrentUser()
            .sink(
                receiveCompletion: { _ in },
                receiveValue: { [weak self] user in
                    self?.user = user
                    self?.isAuthenticated = true
                }
            )
            .store(in: &cancellables)
    }
}
```

## ⚠️ Обработка ошибок

```kotlin
// Kotlin
sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val exception: Exception, val code: Int?) : ApiResult<Nothing>()
    object Loading : ApiResult<Nothing>()
}

class ErrorHandler {
    fun handle(error: Exception): String = when (error) {
        is HttpException -> when (error.code()) {
            400 -> "Неправильные входные данные"
            401 -> "Требуется авторизация"
            409 -> "Пользователь уже существует"
            else -> "Ошибка сервера"
        }
        else -> error.message ?: "Неизвестная ошибка"
    }
}
```

## 🧪 Тестирование

### Kotlin

```kotlin
@RunWith(RobolectricTestRunner::class)
class LoginTest {
    private lateinit var authService: AuthService
    
    @Before
    fun setup() {
        authService = mockk(relaxed = true)
    }
    
    @Test
    fun `should save tokens on successful login`() {
        // Arrange
        val expectedResponse = AuthResponse(
            accessToken = "token",
            refreshToken = "refresh",
            tokenType = "Bearer",
            expiresIn = 3600,
            user = mockk()
        )
        
        coEvery { authService.login(any(), any()) } returns expectedResponse
        
        // Act
        val result = runBlocking { authService.login("test@test.com", "password") }
        
        // Assert
        assertEquals(expectedResponse, result)
    }
}
```

### Swift

```swift
class ApiServiceTests: XCTestCase {
    func testLoginSuccess() {
        let expectation = XCTestExpectation(description: "Login success")
        
        ApiService.shared.login(email: "test@test.com", password: "password")
            .sink(
                receiveCompletion: { completion in
                    if case .failure = completion {
                        XCTFail("Login should succeed")
                    }
                    expectation.fulfill()
                },
                receiveValue: { response in
                    XCTAssertNotNil(response.accessToken)
                    XCTAssertNotNil(response.refreshToken)
                }
            )
            .store(in: &cancellables)
        
        wait(for: [expectation], timeout: 5)
    }
}
```

## 📊 Best Practices

1. **Никогда не логируйте токены**
2. **Используйте HTTPS в production**
3. **Сохраняйте токены в защищенном хранилище**
4. **Обновляйте токены до истечения**
5. **Обрабатывайте 401 ошибки правильно**
6. **Используйте timeouts для запросов**
7. **Логируйте ошибки для debugging**
8. **Тестируйте обновление токенов**

## 🔗 Ссылки

- [API Documentation](./API_DOCUMENTATION.md)
- [Architecture](./ARCHITECTURE.md)
- Retrofit: https://github.com/square/retrofit
- Alamofire: https://github.com/Alamofire/Alamofire
- OkHttp: https://github.com/square/okhttp
