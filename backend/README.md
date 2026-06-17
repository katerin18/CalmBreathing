# Calm Breathing - Backend

REST API сервер для мобильного приложения управления дыхательными упражнениями.

## 📋 Содержание

- [Архитектура](#архитектура)
- [Технологический стек](#технологический-стек)
- [Быстрый старт](#быстрый-старт)
- [API документация](#api-документация)
- [Передача данных с фронта на бэкенд](#передача-данных-с-фронта-на-бэкенд)
- [Структура проекта](#структура-проекта)
- [JWT Авторизация](#jwt-авторизация)

## 🏗️ Архитектура

Полная документация архитектуры доступна в [ARCHITECTURE.md](./ARCHITECTURE.md)

### Основные компоненты

```
┌─────────────────────────────────────────────┐
│      Мобильное приложение (iOS/Android)    │
└────────────────────┬────────────────────────┘
                     │ HTTP/JSON
┌────────────────────▼────────────────────────┐
│         REST API Endpoints                  │
│  /api/auth/* /api/user/*                    │
└────────────────┬───────────────┬────────────┘
                 │               │
    ┌────────────▼┐      ┌──────▼──────────┐
    │   Service   │      │   JWT Security  │
    │   Layer     │      │   Filter        │
    └────────────┬┘      └──────┬──────────┘
                 │               │
    ┌────────────▼───────────────▼────────┐
    │   Repository Layer (JPA)            │
    │   UserRepository, RefreshTokenRepo  │
    └────────────┬───────────────────────┘
                 │
    ┌────────────▼───────────────────────┐
    │   PostgreSQL Database               │
    │   - users, roles, refresh_tokens    │
    └─────────────────────────────────────┘
```

## 🛠️ Технологический стек

- **Framework**: Spring Boot 4.0.5
- **Language**: Java 17
- **Database**: PostgreSQL 13+
- **Authentication**: JWT (JJWT)
- **ORM**: Hibernate (Spring Data JPA)
- **Database Migration**: Liquibase 4.24.0
- **Build Tool**: Maven 3.8+
- **Security**: Spring Security 6.x

## 🚀 Быстрый старт

### Требования

- JDK 17+
- PostgreSQL 13+
- Maven 3.8+

### 1. Клонирование репозитория

```bash
cd backend/calm_breath
```

### 2. Конфигурация БД

Создайте базу данных:

```sql
CREATE DATABASE calm_breath;
CREATE USER calm_user WITH PASSWORD 'your_password';
ALTER ROLE calm_user WITH CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE calm_breath TO calm_user;
```

### 3. Переменные окружения

Экспортируйте или установите в IDE:

```bash
# Linux/Mac
export DB_USERNAME=calm_user
export DB_PASSWORD=your_password
export JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
export MAIL_USERNAME=your_gmail@gmail.com
export MAIL_PASSWORD=your_app_password
export FRONTEND_URL=http://localhost:3000
export ACTUATOR_PASSWORD=admin
```

```cmd
# Windows PowerShell
$env:DB_USERNAME="calm_user"
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
$env:MAIL_USERNAME="your_gmail@gmail.com"
$env:MAIL_PASSWORD="your_app_password"
$env:FRONTEND_URL="http://localhost:3000"
$env:ACTUATOR_PASSWORD="admin"
```

### 4. Сборка и запуск

```bash
# Чистая сборка
mvn clean install

# Запуск приложения
mvn spring-boot:run
```

Приложение будет доступно по адресу: **http://localhost:8080**

### 5. Проверка здоровья приложения

```bash
curl http://localhost:8080/actuator/health
```

Ответ:
```json
{
  "status": "UP"
}
```

## 📖 API документация

### Полная документация

Детальная документация всех endpoints доступна в [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)

### Основные endpoints

#### Аутентификация

```
POST   /api/auth/register     - Регистрация нового пользователя
POST   /api/auth/login        - Вход в систему
POST   /api/auth/refresh      - Обновление access token
POST   /api/auth/logout       - Выход из системы
```

#### Профиль пользователя

```
GET    /api/user/me           - Получить текущего пользователя
GET    /api/user/{userId}     - Получить пользователя по ID
PUT    /api/user/me           - Обновить профиль
PUT    /api/user/password     - Изменить пароль
```

### Пример запроса

```bash
# Регистрация
curl -X POST http://localhost:8080/api/auth/register \\
  -H "Content-Type: application/json" \\
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123",
    "confirmPassword": "SecurePassword123",
    "firstName": "John",
    "lastName": "Doe"
  }'

# Вход
curl -X POST http://localhost:8080/api/auth/login \\
  -H "Content-Type: application/json" \\
  -d '{
    "email": "user@example.com",
    "password": "SecurePassword123"
  }'

# Получить профиль (с токеном)
curl -X GET http://localhost:8080/api/user/me \\
  -H "Authorization: Bearer {accessToken}"
```

## 🔄 Передача данных с фронта на бэкенд

Раздел описывает минимальный контракт обмена данными для мобильного/веб-клиента.

### Базовые правила

- Все запросы отправляются на `http://localhost:8080`
- Для JSON запросов: `Content-Type: application/json`
- Для защищенных endpoint: `Authorization: Bearer {accessToken}`
- Время в API передается в формате ISO-8601, например `2026-05-14T10:30:00`

### Типы запросов по endpoint

| Endpoint | Метод | Как передавать данные с фронта |
|----------|-------|--------------------------------|
| `/api/auth/register` | POST | JSON body (`email`, `password`, `confirmPassword`, `firstName`, `lastName`) |
| `/api/auth/login` | POST | JSON body (`email`, `password`) |
| `/api/auth/refresh` | POST | JSON body (`refreshToken`) |
| `/api/user/me` | GET | Только `Authorization` header |
| `/api/user/me` | PUT | Query/Form params (`firstName`, `lastName`) + `Authorization` |
| `/api/user/password` | PUT | Query/Form params (`oldPassword`, `newPassword`) + `Authorization` |
| `/api/measurements` | POST | JSON body (`startPulse`, `exerciseDurationSeconds`, `endPulse`, `measuredAt`) + `Authorization` |
| `/api/measurements/latest` | GET | Только `Authorization` header |

### Примеры передачи данных

#### 1. Регистрация (JSON body)

```http
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

#### 2. Вход (JSON body)

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "SecurePassword123"
}
```

Ответ содержит `accessToken` и `refreshToken`. На фронте рекомендуется:

1. Хранить `accessToken` и `refreshToken` в защищенном хранилище.
2. Добавлять `accessToken` в `Authorization` для всех защищенных вызовов.
3. При `401 Unauthorized` вызывать `/api/auth/refresh` и повторять исходный запрос.

#### 3. Обновление access token

```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "<refresh-token>"
}
```

#### 4. Обновление профиля (query/form params)

```http
PUT /api/user/me?firstName=John&lastName=Smith
Authorization: Bearer <access-token>
```

#### 5. Отправка замера пульса (JSON body)

```http
POST /api/measurements
Authorization: Bearer <access-token>
Content-Type: application/json

{
  "startPulse": 82,
  "exerciseDurationSeconds": 300,
  "endPulse": 72,
  "measuredAt": "2026-05-14T10:30:00"
}
```

### Общий формат ошибки

При ошибках валидации/авторизации бэкенд возвращает JSON с полями:

```json
{
  "timestamp": "2026-05-14T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Описание ошибки",
  "errorCode": "VALIDATION_ERROR",
  "path": "/api/auth/register"
}
```

Это позволяет фронту единообразно показывать сообщения и обрабатывать коды ошибок.

## 📁 Структура проекта

```
calm_breath/
├── src/main/java/mpi/calmbreath/demo/
│   ├── config/                 # Spring конфигурация
│   │   ├── SecurityConfig      # Spring Security + JWT
│   │   ├── CorsConfig          # CORS настройки
│   │   ├── WebConfig           # Логирование
│   │   └── DataInitializer     # Инициализация данных
│   │
│   ├── controller/             # REST Endpoints
│   │   ├── AuthController      # Аутентификация
│   │   └── UserController      # Профиль пользователя
│   │
│   ├── service/                # Бизнес-логика
│   │   ├── AuthService         # Сервис аутентификации
│   │   └── UserService         # Сервис пользователя
│   │
│   ├── repository/             # Data Access Layer
│   │   ├── UserRepository
│   │   ├── RefreshTokenRepository
│   │   └── RoleRepository
│   │
│   ├── security/               # JWT + Security
│   │   ├── JwtProvider         # Генерация JWT токенов
│   │   ├── JwtAuthFilter       # Валидация токенов
│   │   ├── JwtAuthenticationEntryPoint  # Обработка ошибок
│   │   └── CustomUserDetailsService    # Load User Details
│   │
│   ├── model/                  # Сущности и DTO
│   │   ├── entity/
│   │   │   ├── User
│   │   │   ├── Role
│   │   │   └── RefreshToken
│   │   ├── dto/
│   │   │   ├── request/
│   │   │   ├── response/
│   │   │   └── PageRequest
│   │   └── enums/
│   │       └── UserRole
│   │
│   ├── error/                  # Обработка ошибок
│   │   ├── GlobalExceptionHandler
│   │   ├── CustomException
│   │   └── ErrorResponse
│   │
│   ├── middleware/             # Фильтры
│   │   └── LoggingFilter
│   │
│   └── DemoApplication         # Main приложение
│
├── src/main/resources/
│   ├── application.yml         # Основная конфигурация
│   └── db/changelog/           # Liquibase миграции
│       ├── db.changelog-master.xml
│       └── versions/
│           └── 1.1.xml
│
└── pom.xml                    # Maven конфигурация
```

## 🔐 JWT Авторизация

### Поток аутентификации

```
1. Клиент отправляет credentials (email + password)
   ↓
2. Сервер валидирует учетные данные
   ↓
3. Генерируется пара токенов:
   - Access Token (1 час) - для доступа к API
   - Refresh Token (7 дней) - для обновления access token
   ↓
4. Клиент сохраняет токены в защищенном хранилище
   ↓
5. Для каждого запроса отправляет:
   Authorization: Bearer {accessToken}
   ↓
6. JwtAuthFilter валидирует токен
   ↓
7. При истечении - клиент использует refresh token
   для получения нового access token
```

### Структура JWT

```
Header:
{
  "alg": "HS512",
  "typ": "JWT"
}

Payload (Access Token):
{
  "sub": "user@example.com",
  "userId": "uuid",
  "roles": ["ROLE_USER"],
  "tokenType": "access",
  "iat": 1234567890,
  "exp": 1234571490
}

Signature:
HMACSHA512(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  secret
)
```

### Использование в мобильном приложении

**Kotlin (Android):**

```kotlin
// Сохранение токенов
encryptedPreferences.edit().putString("access_token", accessToken).apply()
encryptedPreferences.edit().putString("refresh_token", refreshToken).apply()

// Использование в запросах
val request = Request.Builder()
    .url("http://api.example.com/api/user/me")
    .addHeader("Authorization", "Bearer $accessToken")
    .build()

// Обновление токена
val tokenResponse = apiService.refreshToken(refreshToken)
encryptedPreferences.edit().putString("access_token", tokenResponse.accessToken).apply()
```

**Swift (iOS):**

```swift
// Сохранение в Keychain
try KeychainService.save(key: "access_token", value: accessToken)
try KeychainService.save(key: "refresh_token", value: refreshToken)

// Использование в запросах
var request = URLRequest(url: url)
if let accessToken = try KeychainService.load(key: "access_token") {
    request.setValue("Bearer \\(accessToken)", forHTTPHeaderField: "Authorization")
}

// Обновление токена
let tokenResponse = try await apiService.refreshToken(refreshToken)
try KeychainService.save(key: "access_token", value: tokenResponse.accessToken)
```

## 🧪 Тестирование

### Postman

Импортируйте коллекцию [Calm_Breathing_API.postman_collection.json](./Calm_Breathing_API.postman_collection.json) в Postman для тестирования всех endpoints.

**Переменные Postman:**
- `base_url` = http://localhost:8080
- `access_token` = (заполняется после login)
- `refresh_token` = (заполняется после login)

### cURL примеры

```bash
# 1. Регистрация
curl -X POST http://localhost:8080/api/auth/register \\
  -H "Content-Type: application/json" \\
  -d '{
    "email": "test@example.com",
    "password": "Test123",
    "confirmPassword": "Test123",
    "firstName": "Test"
  }' | jq .

# 2. Вход
curl -X POST http://localhost:8080/api/auth/login \\
  -H "Content-Type: application/json" \\
  -d '{
    "email": "test@example.com",
    "password": "Test123"
  }' | jq .

# 3. Получить профиль
curl -X GET http://localhost:8080/api/user/me \\
  -H "Authorization: Bearer {accessToken}" | jq .
```

## 📊 Мониторинг

### Actuator Endpoints

```
GET  /actuator/health              - Статус приложения
GET  /actuator/info                - Информация о приложении (future)
GET  /actuator/metrics             - Метрики (future)
GET  /actuator/env                 - Переменные окружения (future)
```

### Логирование

Логи выводятся в консоль и могут быть сохранены в файл:

```yaml
# application.yml
logging:
  level:
    root: INFO
    mpi.calmbreath.demo: DEBUG
  file:
    name: logs/app.log
```

## 🔧 Настройка IDE

### IntelliJ IDEA

1. Откройте `calm_breath` как Maven проект
2. Установите SDK Java 17
3. Перейдите в Run → Edit Configurations
4. Добавьте переменные окружения в Environment variables
5. Запустите с помощью Run → Run 'DemoApplication'

### VS Code

1. Установите расширение "Extension Pack for Java"
2. Откройте папку `calm_breath`
3. Создайте `.vscode/settings.json`:

```json
{
  "java.home": "/path/to/jdk17",
  "maven.executable.preferMavenWrapper": true
}
```

4. Запустите через Command Palette → Java: Start Debugging

## 📝 Лицензия

MIT License

## 👥 Авторы

- Backend Team

## 📞 Поддержка

Для вопросов и проблем создавайте issues в репозитории.

