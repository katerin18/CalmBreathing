# Calm Breathing - Backend API Documentation

## Быстрый старт

### Требования
- Java 17+
- PostgreSQL 13+
- Maven 3.8+

### Установка и запуск

1. **Установка зависимостей**
```bash
mvn clean install
```

2. **Конфигурация переменных окружения**
Создайте файл `.env` или установите переменные окружения:
```
DB_USERNAME=postgres
DB_PASSWORD=your_password
JWT_SECRET=your_base64_encoded_secret_key
MAIL_USERNAME=your_gmail@gmail.com
MAIL_PASSWORD=your_app_password
FRONTEND_URL=http://localhost:3000
```

3. **Запуск приложения**
```bash
mvn spring-boot:run
```

Приложение будет доступно по адресу: `http://localhost:8080`

## API Endpoints

### Аутентификация

#### 1. Регистрация

```
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123",
  "confirmPassword": "SecurePassword123",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Ответ (201 Created):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "enabled": true,
    "emailVerified": false,
    "roles": ["ROLE_USER"],
    "createdAt": "2026-04-20T10:30:00",
    "updatedAt": "2026-04-20T10:30:00"
  }
}
```

#### 2. Вход

```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

**Ответ (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "enabled": true,
    "emailVerified": false,
    "roles": ["ROLE_USER"],
    "createdAt": "2026-04-20T10:30:00",
    "updatedAt": "2026-04-20T10:30:00"
  }
}
```

#### 3. Обновление Access Token

```
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."
}
```

**Ответ (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600000
}
```

#### 4. Выход

```
POST /api/auth/logout
Authorization: Bearer {accessToken}
```

**Ответ (204 No Content)**

---

### Профиль пользователя

#### 1. Получить текущего пользователя

```
GET /api/user/me
Authorization: Bearer {accessToken}
```

**Ответ (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "enabled": true,
  "emailVerified": false,
  "roles": ["ROLE_USER"],
  "createdAt": "2026-04-20T10:30:00",
  "updatedAt": "2026-04-20T10:30:00"
}
```

#### 2. Получить пользователя по ID

```
GET /api/user/{userId}
Authorization: Bearer {accessToken}
```

**Ответ (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "enabled": true,
  "emailVerified": false,
  "roles": ["ROLE_USER"],
  "createdAt": "2026-04-20T10:30:00",
  "updatedAt": "2026-04-20T10:30:00"
}
```

#### 3. Обновить профиль

```
PUT /api/user/me
Authorization: Bearer {accessToken}
Content-Type: application/x-www-form-urlencoded

firstName=John&lastName=Smith
```

**Ответ (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "firstName": "John",
  "lastName": "Smith",
  "enabled": true,
  "emailVerified": false,
  "roles": ["ROLE_USER"],
  "createdAt": "2026-04-20T10:30:00",
  "updatedAt": "2026-04-20T10:35:00"
}
```

#### 4. Изменить пароль

```
PUT /api/user/password
Authorization: Bearer {accessToken}
Content-Type: application/x-www-form-urlencoded

oldPassword=OldPassword123&newPassword=NewPassword123
```

**Ответ (204 No Content)**

---

## Обработка ошибок

Все ошибки возвращаются в следующем формате:

```json
{
  "timestamp": "2026-04-20T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Пароли не совпадают",
  "errorCode": "PASSWORD_MISMATCH",
  "path": "/api/auth/register"
}
```

### Коды ошибок

| Code | HTTP Status | Описание |
|------|------------|---------|
| `VALIDATION_ERROR` | 400 | Ошибка валидации входных данных |
| `PASSWORD_MISMATCH` | 400 | Пароли не совпадают |
| `INVALID_CREDENTIALS` | 401 | Неправильный email или пароль |
| `UNAUTHORIZED` | 401 | Требуется авторизация |
| `USER_DISABLED` | 403 | Пользователь отключен |
| `USER_NOT_FOUND` | 404 | Пользователь не найден |
| `USER_ALREADY_EXISTS` | 409 | Пользователь с таким email уже существует |
| `REFRESH_TOKEN_NOT_FOUND` | 401 | Refresh token не найден |
| `REFRESH_TOKEN_EXPIRED` | 401 | Refresh token истек |
| `INTERNAL_SERVER_ERROR` | 500 | Внутренняя ошибка сервера |

---

## JWT Token

### Access Token (1 час)

```json
{
  "sub": "user@example.com",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "roles": ["ROLE_USER"],
  "iat": 1234567890,
  "exp": 1234571490
}
```

### Refresh Token (7 дней)

Хранится в БД и может быть аннулирован при выходе.

---

## Интеграция с мобильным приложением

### Пример на Kotlin/Android

```kotlin
// 1. Регистрация
val registerRequest = RegisterRequest(
    email = "user@example.com",
    password = "SecurePassword123",
    confirmPassword = "SecurePassword123",
    firstName = "John",
    lastName = "Doe"
)

val authResponse = apiService.register(registerRequest).await()
val accessToken = authResponse.accessToken
val refreshToken = authResponse.refreshToken

// Сохранить токены в защищенном хранилище
securePreferences.save("access_token", accessToken)
securePreferences.save("refresh_token", refreshToken)

// 2. Использование в запросах
val authHeader = "Bearer $accessToken"
val response = apiService.getCurrentUser(authHeader).await()

// 3. Обновление токена при истечении
val tokenRequest = RefreshTokenRequest(refreshToken)
val newTokenResponse = apiService.refreshToken(tokenRequest).await()
val newAccessToken = newTokenResponse.accessToken

// Обновить сохраненный токен
securePreferences.save("access_token", newAccessToken)
```

### Пример на Swift/iOS

```swift
// 1. Регистрация
let registerRequest = RegisterRequest(
    email: "user@example.com",
    password: "SecurePassword123",
    confirmPassword: "SecurePassword123",
    firstName: "John",
    lastName: "Doe"
)

apiService.register(registerRequest) { result in
    switch result {
    case .success(let authResponse):
        let accessToken = authResponse.accessToken
        let refreshToken = authResponse.refreshToken
        
        // Сохранить в KeyChain
        KeychainService.save(key: "access_token", value: accessToken)
        KeychainService.save(key: "refresh_token", value: refreshToken)
        
    case .failure(let error):
        print("Registration failed: \(error)")
    }
}

// 2. Использование в запросах
let headers = ["Authorization": "Bearer \(accessToken)"]
apiService.getCurrentUser(headers: headers) { result in
    switch result {
    case .success(let user):
        print("User: \(user)")
    case .failure(let error):
        print("Error: \(error)")
    }
}

// 3. Обновление токена
let refreshRequest = RefreshTokenRequest(refreshToken: refreshToken)
apiService.refreshToken(refreshRequest) { result in
    switch result {
    case .success(let tokenResponse):
        let newAccessToken = tokenResponse.accessToken
        KeychainService.save(key: "access_token", value: newAccessToken)
    case .failure(let error):
        print("Token refresh failed: \(error)")
    }
}
```

---

## Безопасность

### Рекомендации

1. **Сохранение токенов**
   - Используйте защищенное хранилище (Keychain на iOS, KeyStore на Android)
   - НЕ сохраняйте в SharedPreferences/UserDefaults

2. **Обновление токенов**
   - Обновляйте Access Token перед его истечением
   - Используйте Refresh Token для получения новых токенов

3. **Безопасность по сети**
   - Всегда используйте HTTPS
   - Проверяйте SSL сертификаты

4. **Обработка ошибок**
   - При 401 ошибке - попробуйте обновить токен
   - При повторной 401 после refresh - перенаправьте на вход

---

## CORS

API поддерживает CORS для мобильных приложений:

```
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
Access-Control-Allow-Headers: *
Access-Control-Allow-Credentials: true
Access-Control-Max-Age: 3600
```

---

## Мониторинг

### Health Check

```
GET /actuator/health
```

**Ответ:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

---

## Развертывание

### Development
```bash
mvn spring-boot:run
```

### Production
```bash
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --server.port=8080
```

---

## Поддержка

При возникновении проблем:
1. Проверьте логи приложения
2. Убедитесь что БД запущена
3. Проверьте правильность переменных окружения
4. Посмотрите ARCHITECTURE.md для понимания структуры
