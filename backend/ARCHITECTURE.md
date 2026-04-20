# Архитектура Calm Breathing Backend

## Обзор системы

REST API сервер для мобильного приложения управления дыхательными упражнениями с JWT авторизацией.

## Технологический стек

- **Framework**: Spring Boot 4.0.5
- **Language**: Java 17
- **Database**: PostgreSQL
- **Security**: Spring Security + JWT (JJWT)
- **API Format**: REST JSON
- **Database Migration**: Liquibase
- **Build Tool**: Maven

## Архитектура слоев

```
┌─────────────────────────────────────┐
│         Mobile Application          │ HTTP/JSON
├─────────────────────────────────────┤
│           REST Controller            │
├─────────────────────────────────────┤
│   Service Layer (Business Logic)    │
├─────────────────────────────────────┤
│      Repository Layer (Data)        │
├─────────────────────────────────────┤
│    Security Layer (JWT, Auth)       │
├─────────────────────────────────────┤
│  Spring Security + JWT Filter       │
├─────────────────────────────────────┤
│         PostgreSQL Database         │
└─────────────────────────────────────┘
```

## Структура пакетов

```
mpi.calmbreath.demo
├── config/              # Конфигурация приложения
│   ├── SecurityConfig   # Конфиг Spring Security
│   ├── CorsConfig       # CORS политика
│   └── WebConfig        # Общая конфигурация
│
├── controller/          # REST endpoints
│   ├── AuthController   # Регистрация, вход, токены
│   ├── UserController   # Профиль пользователя
│   └── ExerciseController # Упражнения (расширение)
│
├── service/             # Бизнес-логика
│   ├── AuthService      # Аутентификация
│   ├── UserService      # Управление пользователями
│   └── TokenService     # Управление токенами
│
├── repository/          # Доступ к данным
│   ├── UserRepository   # Запросы пользователей
│   └── RefreshTokenRepository # Управление refresh токенами
│
├── security/            # Безопасность
│   ├── JwtProvider      # Генерация и валидация JWT
│   ├── JwtFilter        # Фильтр JWT
│   ├── JwtAuthenticationEntryPoint # Обработка ошибок auth
│   └── CustomUserDetailsService    # Load User Details
│
├── model/               # Сущности и DTO
│   ├── entity/
│   │   ├── User
│   │   ├── Role
│   │   └── RefreshToken
│   ├── dto/
│   │   ├── request/
│   │   │   ├── LoginRequest
│   │   │   ├── RegisterRequest
│   │   │   └── RefreshTokenRequest
│   │   └── response/
│   │       ├── AuthResponse
│   │       ├── UserResponse
│   │       └── TokenResponse
│   └── enums/
│       └── UserRole
│
├── error/               # Обработка ошибок
│   ├── GlobalExceptionHandler  # Глобальная обработка
│   ├── CustomException         # Пользовательские исключения
│   └── ErrorResponse           # Формат ошибок
│
├── middleware/          # Фильтры и интерцепторы
│   ├── LoggingFilter    # Логирование запросов
│   └── RateLimitFilter  # Ограничение частоты (опционально)
│
└── DemoApplication      # Main приложение
```

## Поток аутентификации

```
1. РЕГИСТРАЦИЯ
   POST /api/auth/register
   ├─ Валидация данных
   ├─ Проверка уникальности email
   ├─ Хеширование пароля
   ├─ Сохранение в БД
   └─ Возврат токенов (Access + Refresh)

2. ВХОД
   POST /api/auth/login
   ├─ Валидация учетных данных
   ├─ Проверка пароля
   ├─ Генерация JWT токенов
   ├─ Сохранение Refresh Token в БД
   └─ Возврат токенов

3. ЗАПРОС К API
   GET/POST/PUT /api/user/*
   ├─ JwtFilter перехватывает запрос
   ├─ Извлекает токен из заголовка Authorization
   ├─ Валидирует подпись JWT
   ├─ Загружает пользователя в SecurityContext
   └─ Обрабатывает запрос

4. ОБНОВЛЕНИЕ ТОКЕНА
   POST /api/auth/refresh
   ├─ Валидация Refresh Token
   ├─ Проверка в БД
   ├─ Генерация нового Access Token
   └─ Возврат нового Access Token

5. ВЫХОД
   POST /api/auth/logout
   ├─ Удаление Refresh Token из БД
   └─ Очистка сессии на клиенте
```

## Модели данных

### User (Пользователь)
```
- id (UUID)
- email (unique, not null)
- password (hashed, not null)
- firstName (nullable)
- lastName (nullable)
- roles (many-to-many with Role)
- enabled (boolean, default=true)
- emailVerified (boolean, default=false)
- createdAt (timestamp)
- updatedAt (timestamp)
```

### RefreshToken
```
- id (UUID)
- token (unique, not null)
- userId (foreign key)
- expiresAt (timestamp)
- createdAt (timestamp)
```

### Role (Роль)
```
- id (UUID)
- name (enum: ROLE_USER, ROLE_ADMIN)
```

## JWT Token Structure

### Access Token (1 час)
```json
{
  "sub": "user@example.com",
  "userId": "uuid",
  "roles": ["ROLE_USER"],
  "iat": 1234567890,
  "exp": 1234571490
}
```

### Refresh Token (7 дней)
```json
{
  "sub": "user@example.com",
  "userId": "uuid",
  "type": "refresh",
  "iat": 1234567890,
  "exp": 1234571490
}
```

## REST API Endpoints

### Authentication
- `POST /api/auth/register` - Регистрация
- `POST /api/auth/login` - Вход
- `POST /api/auth/logout` - Выход
- `POST /api/auth/refresh` - Обновление токена

### User
- `GET /api/user/me` - Текущий пользователь
- `PUT /api/user/me` - Обновить профиль
- `PUT /api/user/password` - Изменить пароль

### Admin (будущее расширение)
- `GET /api/admin/users` - Список пользователей
- `DELETE /api/admin/users/{id}` - Удалить пользователя

## Безопасность

### CORS Policy
- Разрешены запросы с мобильных приложений
- Localhost для разработки
- Production домены по конфигурации

### Защита от уязвимостей
- CSRF защита отключена для stateless API
- HSTS headers
- XSS protection
- Content-Type validation
- Rate limiting (future)

### Хеширование паролей
- Algorithm: BCrypt
- Strength: 12 rounds (default Spring Security)

## Обработка ошибок

Все ошибки возвращаются в формате:
```json
{
  "timestamp": "2026-04-20T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Описание ошибки",
  "path": "/api/auth/login"
}
```

### Коды ошибок
- `400` - Неправильные входные данные
- `401` - Не авторизован
- `403` - Доступ запрещен
- `404` - Ресурс не найден
- `409` - Конфликт (email уже существует)
- `500` - Ошибка сервера

## Документирование API

Документация доступна через:
- Swagger/OpenAPI (future): `/swagger-ui.html`
- Postman collection в репозитории

## Мониторинг и логирование

- Request/Response logging на уровне фильтра
- Exception logging в GlobalExceptionHandler
- Security event logging
- Database query logging (dev mode)

## Развертывание

### Development
```bash
mvn spring-boot:run
```

### Production Build
```bash
mvn clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Environment Variables
```
DB_USERNAME=postgres
DB_PASSWORD=password
JWT_SECRET=base64_encoded_secret_key
MAIL_USERNAME=gmail_username
MAIL_PASSWORD=gmail_app_password
FRONTEND_URL=https://yourfrontend.com
```
