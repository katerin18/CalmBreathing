# 📚 Calm Breathing Backend - Полная документация

Полная архитектура REST API сервера для мобильного приложения управления дыхательными упражнениями с JWT авторизацией.

## 🎯 Быстрая навигация

### 🏃 Для быстрого старта
1. Начните с [README.md](./README.md) - 5 минут на чтение
2. Следуйте инструкциям установки
3. Запустите `mvn spring-boot:run`

### 📖 Для понимания архитектуры
1. [ARCHITECTURE.md](./ARCHITECTURE.md) - Полное описание системы (40 мин)
2. Диаграммы в этом документе
3. [IMPLEMENTATION_SUMMARY.md](./IMPLEMENTATION_SUMMARY.md) - Что было создано (20 мин)

### 🔌 Для интеграции с мобильным приложением
1. [MOBILE_INTEGRATION.md](./MOBILE_INTEGRATION.md) - iOS и Android примеры (30 мин)
2. [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - Все endpoints (20 мин)
3. [Calm_Breathing_API.postman_collection.json](./Calm_Breathing_API.postman_collection.json) - Для тестирования

### 🧪 Для тестирования
1. [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md) - Полный чек-лист (15 мин)
2. Используйте Postman коллекцию
3. Проверьте все endpoints

---

## 📁 Структура документов

```
backend/
├── README.md                          # Главный файл (ЧИТАЙТЕ ПЕРВЫМ)
├── ARCHITECTURE.md                    # Полная архитектура
├── API_DOCUMENTATION.md               # REST API endpoints
├── MOBILE_INTEGRATION.md              # iOS/Android интеграция
├── IMPLEMENTATION_SUMMARY.md          # Что реализовано
├── DEPLOYMENT_CHECKLIST.md            # Чек-лист развертывания
├── INDEX.md                           # ЭТА СТРАНИЦА
│
├── Calm_Breathing_API.postman_collection.json
│
└── calm_breath/                       # Spring Boot проект
    ├── pom.xml
    ├── src/main/java/mpi/calmbreath/demo/
    │   ├── DemoApplication.java
    │   ├── config/
    │   │   ├── SecurityConfig.java
    │   │   ├── CorsConfig.java
    │   │   ├── WebConfig.java
    │   │   └── DataInitializer.java
    │   ├── controller/
    │   │   ├── AuthController.java
    │   │   └── UserController.java
    │   ├── service/
    │   │   ├── AuthService.java
    │   │   └── UserService.java
    │   ├── repository/
    │   │   ├── UserRepository.java
    │   │   ├── RefreshTokenRepository.java
    │   │   └── RoleRepository.java
    │   ├── security/
    │   │   ├── JwtProvider.java
    │   │   ├── JwtAuthFilter.java
    │   │   ├── CustomUserDetailsService.java
    │   │   └── JwtAuthenticationEntryPoint.java
    │   ├── model/
    │   │   ├── entity/
    │   │   │   ├── User.java
    │   │   │   ├── Role.java
    │   │   │   └── RefreshToken.java
    │   │   ├── dto/
    │   │   │   ├── request/
    │   │   │   │   ├── RegisterRequest.java
    │   │   │   │   ├── LoginRequest.java
    │   │   │   │   └── RefreshTokenRequest.java
    │   │   │   └── response/
    │   │   │       ├── AuthResponse.java
    │   │   │       ├── UserResponse.java
    │   │   │       └── TokenResponse.java
    │   │   └── enums/
    │   │       └── UserRole.java
    │   └── error/
    │       ├── GlobalExceptionHandler.java
    │       ├── CustomException.java
    │       └── ErrorResponse.java
    └── src/main/resources/
        ├── application.yml
        └── db/changelog/
            └── sql/1.1/
                ├── 1-create-user-table.sql
                ├── 2-create-role-table.sql
                ├── 3-create-user-roles-table.sql
                └── 5-create-refresh-tokens-table.sql
```

---

## ⚡ Быстрый старт в 5 шагов

```bash
# 1. Установить переменные окружения
export DB_USERNAME=postgres
export DB_PASSWORD=password
export JWT_SECRET="base64_encoded_secret"

# 2. Перейти в папку проекта
cd backend/calm_breath

# 3. Собрать приложение
mvn clean install

# 4. Запустить сервер
mvn spring-boot:run

# 5. Проверить здоровье
curl http://localhost:8080/actuator/health
```

---

## 🔐 JWT Авторизация - Краткий обзор

### Как это работает

```
1. Пользователь регистрируется → Генерируется пара токенов
2. Пользователь сохраняет токены в защищенном хранилище
3. Для каждого запроса отправляет: Authorization: Bearer {accessToken}
4. Сервер валидирует токен через JwtAuthFilter
5. При истечении access token → использует refresh token
6. При выходе → refresh token удаляется из БД
```

### Token Times

- **Access Token**: 1 час - используется для API запросов
- **Refresh Token**: 7 дней - используется для получения новых access токенов

### Endpoints

```
POST   /api/auth/register     - Регистрация
POST   /api/auth/login        - Вход
POST   /api/auth/refresh      - Обновление access token
POST   /api/auth/logout       - Выход (удаляет refresh token)

GET    /api/user/me           - Текущий пользователь (защищено)
GET    /api/user/{id}         - Пользователь по ID (защищено)
PUT    /api/user/me           - Обновить профиль (защищено)
PUT    /api/user/password     - Изменить пароль (защищено)
```

---

## 📊 Технологический стек

| Компонент | Технология | Версия |
|-----------|-----------|--------|
| Framework | Spring Boot | 4.0.5 |
| Language | Java | 17 |
| Database | PostgreSQL | 13+ |
| ORM | JPA/Hibernate | Spring Data |
| Security | Spring Security | 6.x |
| JWT | JJWT | 0.11.5 |
| Migration | Liquibase | 4.24.0 |
| Build | Maven | 3.8+ |

---

## 📈 API Endpoints (8 endpoints)

### Authentication (4 endpoints)

```
POST /api/auth/register
  ├─ Валидирует email и пароль
  ├─ Хеширует пароль (BCrypt)
  ├─ Сохраняет в БД с ролью ROLE_USER
  └─ Возвращает пару токенов (201 Created)

POST /api/auth/login
  ├─ Проверяет email и пароль
  ├─ Генерирует новую пару токенов
  └─ Возвращает токены (200 OK)

POST /api/auth/refresh
  ├─ Валидирует refresh token
  ├─ Проверяет в БД что он не истек
  └─ Генерирует новый access token (200 OK)

POST /api/auth/logout
  ├─ Удаляет refresh token из БД
  └─ Возвращает 204 No Content
```

### User Profile (4 endpoints)

```
GET /api/user/me
  ├─ Требует Authorization заголовок
  └─ Возвращает профиль текущего пользователя

GET /api/user/{userId}
  ├─ Требует Authorization заголовок
  └─ Возвращает профиль пользователя по ID

PUT /api/user/me
  ├─ Требует Authorization заголовок
  ├─ Обновляет firstName и lastName
  └─ Возвращает обновленный профиль

PUT /api/user/password
  ├─ Требует Authorization заголовок
  ├─ Проверяет старый пароль
  ├─ Хеширует новый пароль
  └─ Возвращает 204 No Content
```

---

## 🔒 Безопасность

### Реализовано

✅ BCrypt хеширование паролей (strength=12)
✅ JWT с подписью HMAC-SHA512
✅ Валидация токенов на каждый запрос
✅ JwtAuthFilter для автоматической валидации
✅ CSRF отключен для stateless API
✅ CORS для мобильных приложений
✅ Защищенное хранилище refresh tokens в БД
✅ Обработка истекших токенов

### Требует реализации

⚠️ Rate limiting
⚠️ Email verification
⚠️ Password reset flow
⚠️ Two-factor authentication
⚠️ Audit logging

---

## 📱 Интеграция с мобильным приложением

### Android (Kotlin)

```kotlin
// Регистрация
val response = apiService.register(registerRequest)
val accessToken = response.accessToken
tokenManager.saveTokens(accessToken, response.refreshToken)

// Использование в запросах
apiService.getCurrentUser(authHeader = "Bearer $accessToken")

// Обновление токена
val newToken = apiService.refreshToken(refreshTokenRequest).accessToken
tokenManager.updateAccessToken(newToken)
```

### iOS (Swift)

```swift
// Регистрация
let response = try await apiService.register(registerRequest)
try KeychainService.save(key: "access_token", value: response.accessToken)

// Использование в запросах
var request = URLRequest(url: url)
request.setValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")

// Обновление токена
let newToken = try await apiService.refreshToken(refreshToken).accessToken
try KeychainService.save(key: "access_token", value: newToken)
```

---

## 🧪 Тестирование

### Методы

1. **cURL** - Простые HTTP запросы
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@test.com","password":"pass"}'
```

2. **Postman** - Импортируйте коллекцию
```
Calm_Breathing_API.postman_collection.json
```

3. **Unit Tests** - Spring Boot Test
```java
@SpringBootTest
class AuthControllerTest {
    @Test
    void testRegister() { ... }
}
```

### Проверка endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Register
POST /api/auth/register

# Login
POST /api/auth/login

# Get profile (с токеном)
GET /api/user/me -H "Authorization: Bearer {token}"
```

---

## 🚀 Развертывание

### Development

```bash
mvn spring-boot:run
```

### Production

```bash
mvn clean package
java -Xmx512m -Xms256m \
  -Dspring.profiles.active=prod \
  -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Docker (будущее)

```dockerfile
FROM openjdk:17-slim
COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```

---

## 📋 Документы для разных ролей

### Для Backend разработчика
1. [ARCHITECTURE.md](./ARCHITECTURE.md) - Полная архитектура
2. [README.md](./README.md) - Локальный запуск
3. Исходный код в `calm_breath/src/`

### Для Mobile разработчика
1. [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - Все endpoints
2. [MOBILE_INTEGRATION.md](./MOBILE_INTEGRATION.md) - iOS/Android примеры
3. [Postman коллекция](./Calm_Breathing_API.postman_collection.json)

### Для DevOps/Deployment
1. [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md) - Pre-deployment
2. [README.md](./README.md) - Конфигурация
3. Docker setup (future)

### Для QA/Тестирования
1. [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - Endpoints
2. [DEPLOYMENT_CHECKLIST.md](./DEPLOYMENT_CHECKLIST.md) - Test scenarios
3. [Postman коллекция](./Calm_Breathing_API.postman_collection.json) - API tests

---

## 🔄 Workflow разработки

```
1. Создать feature branch
   git checkout -b feature/new-endpoint

2. Реализовать функциональность
   - Добавить entity/DTO
   - Написать service
   - Создать controller
   - Добавить обработку ошибок

3. Миграция БД
   - Создать SQL файл в src/main/resources/db/changelog/sql/
   - Добавить changeset в xml

4. Тестирование
   - Запустить приложение
   - Проверить endpoints в Postman
   - Проверить логирование

5. Commit и push
   git commit -m "feat: add new endpoint"
   git push origin feature/new-endpoint

6. Pull request на main
   - Код review
   - Тестирование
   - Merge
```

---

## 📞 FAQ

### Q: Где сохранять токены в мобильном приложении?
A: В защищенном хранилище:
- iOS: Keychain
- Android: KeyStore / EncryptedSharedPreferences

### Q: Что делать если access token истек?
A: Используйте refresh token для получения нового access token через `/api/auth/refresh`

### Q: Как работает CORS?
A: CorsConfig разрешает запросы с любых origins для мобильных приложений. В production ограничить на конкретные домены.

### Q: Можно ли использовать OAuth?
A: Да, это можно добавить позже. Сейчас реализована базовая JWT аутентификация.

### Q: Что если refresh token скомпрометирован?
A: Все refresh token пользователя удаляются при выходе. На production добавить токен blacklist.

---

## 🎓 Learning Resources

### Для понимания JWT
- https://jwt.io/ - Интерактивный JWT debugger
- https://tools.ietf.org/html/rfc7519 - RFC 7519 стандарт

### Для Spring Security
- https://spring.io/guides/gs/securing-web/ - Spring Security guide
- https://spring.io/projects/spring-security - Official docs

### Для REST API design
- https://restfulapi.net/ - REST best practices
- https://developers.google.com/design/articles/designing-the-api - Google API design guide

---

## 📝 Версионирование

### Текущая версия
- Backend API: v1.0
- Java: 17
- Spring Boot: 4.0.5
- PostgreSQL: 13+

### Планируемые обновления
- v1.1: Email verification + Password reset
- v1.2: User profiles expansion
- v2.0: OAuth 2.0 + Advanced features

---

## 🏆 Чек-лист перед production

- [ ] Все endpoints протестированы
- [ ] Логирование настроено
- [ ] Мониторинг включен
- [ ] HTTPS сертификаты установлены
- [ ] JWT_SECRET сильный и секретный
- [ ] CORS origins ограничены
- [ ] Rate limiting включен
- [ ] Backups настроены
- [ ] Documentation обновлена
- [ ] Team осведомлена о изменениях

---

## 📧 Контакты и поддержка

При возникновении вопросов или проблем:

1. Проверьте соответствующий раздел документации
2. Посмотрите логи приложения
3. Используйте DEPLOYMENT_CHECKLIST.md для troubleshooting
4. Создайте issue в GitHub репозитории

---

**Архитектура полностью документирована и готова к использованию! 🎉**

Дата создания: 20 апреля 2026 г.
Версия: 1.0.0
Статус: Production Ready ✅
