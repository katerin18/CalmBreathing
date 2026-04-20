# 🚀 Deployment & Testing Checklist

## ✅ Pre-Deployment Checklist

### Окружение

- [ ] Java 17 JDK установлен
- [ ] PostgreSQL 13+ запущен и доступен
- [ ] Maven 3.8+ установлен
- [ ] Git конфигурирован

### Переменные окружения

- [ ] `DB_USERNAME` установлен
- [ ] `DB_PASSWORD` установлен
- [ ] `JWT_SECRET` установлен (base64 encoded, 32 bytes minimum)
- [ ] `MAIL_USERNAME` установлен (если используется отправка email)
- [ ] `MAIL_PASSWORD` установлен
- [ ] `FRONTEND_URL` установлен для CORS

### База данных

- [ ] PostgreSQL запущен
- [ ] База `calm_breath` создана
- [ ] Пользователь `calm_user` создан с правами
- [ ] Соединение тестировано (psql подключение)

```sql
-- Проверка
\l                    # список баз данных
\du                   # список пользователей
SELECT datname FROM pg_database WHERE datname = 'calm_breath';
```

---

## 🔨 Build & Compilation

### Очистка и сборка

```bash
cd backend/calm_breath

# Очистка старых артефактов
mvn clean

# Сборка проекта
mvn install

# Проверка на ошибки компиляции
mvn compile
```

### Проверка зависимостей

```bash
# Скачивание всех зависимостей
mvn dependency:resolve

# Проверка на конфликты
mvn dependency:tree

# Обновление snapshot зависимостей
mvn install -U
```

---

## 🎬 Application Startup

### Запуск приложения

```bash
# Способ 1: Maven Spring Boot
mvn spring-boot:run

# Способ 2: JAR файл
mvn package
java -jar target/demo-0.0.1-SNAPSHOT.jar

# Способ 3: IDE (IntelliJ IDEA / VS Code)
# Запустить DemoApplication.java
```

### Проверка запуска

Приложение должно вывести что-то подобное:

```
2026-04-20 10:30:00.000  INFO 12345 --- [main] m.c.d.DemoApplication : Starting DemoApplication
...
2026-04-20 10:30:05.000  INFO 12345 --- [main] o.s.b.a.w.s.WelcomePageHandlerMapping : Adding welcome page
2026-04-20 10:30:05.000  INFO 12345 --- [main] m.c.d.DemoApplication : Started DemoApplication in 5.234 seconds
```

---

## 🧪 API Testing

### 1. Health Check

```bash
curl -i http://localhost:8080/actuator/health
```

**Ожидается:**
```
HTTP/1.1 200 OK
Content-Type: application/json

{"status":"UP"}
```

### 2. Регистрация

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123",
    "confirmPassword": "TestPass123",
    "firstName": "Test",
    "lastName": "User"
  }' | jq .
```

**Ожидается:**
```
HTTP/1.1 201 Created
{
  "accessToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 3600000,
  "user": {...}
}
```

### 3. Вход

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "TestPass123"
  }' | jq .
```

### 4. Получить профиль (с токеном)

```bash
# Сохраните accessToken из предыдущего ответа
export TOKEN="eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8080/api/user/me \
  -H "Authorization: Bearer $TOKEN" | jq .
```

**Ожидается:**
```
HTTP/1.1 200 OK
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "test@example.com",
  "firstName": "Test",
  "lastName": "User",
  "enabled": true,
  "emailVerified": false,
  "roles": ["ROLE_USER"],
  "createdAt": "2026-04-20T10:30:00",
  "updatedAt": "2026-04-20T10:30:00"
}
```

### 5. Обновление профиля

```bash
curl -X PUT "http://localhost:8080/api/user/me?firstName=Updated&lastName=Name" \
  -H "Authorization: Bearer $TOKEN" | jq .
```

### 6. Изменение пароля

```bash
curl -X PUT "http://localhost:8080/api/user/password?oldPassword=TestPass123&newPassword=NewPass123" \
  -H "Authorization: Bearer $TOKEN"
```

**Ожидается:**
```
HTTP/1.1 204 No Content
```

### 7. Обновление токена

```bash
export REFRESH_TOKEN="eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8080/api/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}" | jq .
```

### 8. Выход

```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer $TOKEN"
```

**Ожидается:**
```
HTTP/1.1 204 No Content
```

---

## 🔍 Database Verification

### Проверка структуры таблиц

```sql
-- Подключиться к базе
psql -U calm_user -d calm_breath

-- Проверить таблицы
\dt

-- Проверить структуру users
\d users

-- Проверить пользователей
SELECT id, email, enabled FROM users LIMIT 5;

-- Проверить роли
SELECT * FROM roles;

-- Проверить refresh tokens
SELECT id, user_id, expires_at, revoked FROM refresh_tokens;
```

### Очистка данных для переработки

```sql
-- Очистить всех пользователей
DELETE FROM user_roles;
DELETE FROM users;
DELETE FROM refresh_tokens;

-- Сбросить последовательности (если используются)
-- ALTER SEQUENCE users_id_seq RESTART WITH 1;

-- Проверить что очищено
SELECT COUNT(*) FROM users;
```

---

## 🧪 Postman Testing

### Импорт коллекции

1. Откройте Postman
2. Click "Import"
3. Выберите `Calm_Breathing_API.postman_collection.json`
4. Click "Import"

### Установка переменных

1. Перейдите в Collection → Variables
2. Установите значения:
   - `base_url`: `http://localhost:8080`
   - `access_token`: (заполняется после login)
   - `refresh_token`: (заполняется после login)

### Запуск тестов

1. Запустите Register endpoint
2. Скопируйте `accessToken` из ответа
3. Установите в переменную `{{access_token}}`
4. Запустите остальные endpoints

---

## 📊 Performance Testing

### Load Testing с ApacheBench

```bash
# Проверить возможность обработки запросов
ab -n 100 -c 10 http://localhost:8080/actuator/health

# Результаты
This is ApacheBench, Version 2.3
Benchmarking localhost (be patient)...
...
Requests per second:    1000 [#/sec] (mean)
Time per request:       10.000 [ms] (mean)
```

### Stress Testing с JMeter

```bash
# Установка JMeter
brew install jmeter  # macOS
choco install jmeter # Windows

# Запуск JMeter GUI
jmeter

# Или запуск в headless режиме
jmeter -n -t test_plan.jmx -l results.jtl -j jmeter.log
```

---

## 🔐 Security Verification

### Проверка BCrypt хеширования

```bash
# Подключиться к БД и проверить пароль
psql -U calm_user -d calm_breath

SELECT email, password FROM users WHERE email = 'test@example.com';

-- Результат должен быть BCrypt hash:
-- $2a$12$abc123def456...
```

### Проверка JWT валидности

```bash
# Используйте сайт https://jwt.io для декодирования
# 1. Скопируйте accessToken
# 2. Вставьте на jwt.io
# 3. Проверьте payload:
{
  "sub": "test@example.com",
  "userId": "...",
  "roles": ["ROLE_USER"],
  "tokenType": "access",
  "iat": 1234567890,
  "exp": 1234571490
}
```

### CORS тестирование

```bash
# Проверить CORS headers
curl -i -X OPTIONS http://localhost:8080/api/user/me \
  -H "Origin: http://localhost:3000" \
  -H "Access-Control-Request-Method: GET"

# Должны быть заголовки:
# Access-Control-Allow-Origin: http://localhost:3000
# Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH
```

---

## 📝 Logging & Debugging

### Включение Debug логирования

Обновите `application.yml`:

```yaml
logging:
  level:
    root: INFO
    mpi.calmbreath.demo: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

### Просмотр логов

```bash
# Хвост логов в реальном времени
tail -f logs/app.log | grep "ERROR\|WARN"

# Поиск конкретной ошибки
grep "CustomException" logs/app.log

# Количество ошибок
grep "ERROR" logs/app.log | wc -l
```

### Отладка в IDE

1. Установите breakpoint в коде
2. Run → Debug DemoApplication
3. Используйте Debug Console для инспекции переменных

---

## 🐛 Troubleshooting

### Проблема: Database Connection Refused

```
org.postgresql.util.PSQLException: Connection to localhost:5432 refused
```

**Решение:**
```bash
# Проверьте что PostgreSQL запущен
psql -U postgres -d postgres

# Убедитесь что БД создана
createdb calm_breath -U postgres

# Проверьте переменные окружения
echo $DB_USERNAME
echo $DB_PASSWORD
```

### Проблема: Port 8080 Already in Use

```
Address already in use
```

**Решение:**
```bash
# Найдите процесс на порту 8080
lsof -i :8080

# Убейте процесс
kill -9 <PID>

# Или используйте другой порт
java -jar target/demo-0.0.1-SNAPSHOT.jar --server.port=8081
```

### Проблема: JWT Token Validation Failed

```
MalformedJwtException: Unable to read the JWT
```

**Решение:**
```bash
# Проверьте JWT_SECRET
echo $JWT_SECRET

# Должен быть base64 encoded 32+ байта
# Сгенерируйте новый:
openssl rand -base64 32

# Экспортируйте новый secret
export JWT_SECRET="generated_secret"
```

### Проблема: CORS ошибка в браузере

```
Access to XMLHttpRequest blocked by CORS policy
```

**Решение:**
- Проверьте что CORS включен в SecurityConfig
- Проверьте что origem разрешен в CorsConfig
- Используйте Postman/cURL для тестирования без CORS

---

## ✨ Optimization Tips

### 1. Database Query Performance

```java
// Используйте @EntityGraph для eager loading
@Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = ?1")
Optional<User> findByEmailWithRoles(String email);
```

### 2. Caching (Future Implementation)

```java
@Cacheable("users")
public User getUserById(String userId) {
    return userRepository.findById(userId).orElseThrow();
}
```

### 3. Connection Pooling

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
```

---

## 📋 Production Deployment

### Перед развертыванием

- [ ] Отключить debug логирование
- [ ] Установить HTTPS (SSL certificates)
- [ ] Ограничить CORS origins
- [ ] Установить strong JWT_SECRET
- [ ] Включить Rate Limiting
- [ ] Настроить Email (или отключить)
- [ ] Настроить backups
- [ ] Настроить monitoring

### Команда запуска

```bash
java -Xmx512m -Xms256m \
  -Dspring.profiles.active=prod \
  -Dserver.port=8080 \
  -e DB_USERNAME=prod_user \
  -e DB_PASSWORD=$PROD_PASSWORD \
  -e JWT_SECRET=$PROD_JWT_SECRET \
  -jar target/demo-0.0.1-SNAPSHOT.jar
```

---

## 🎯 Next Steps

1. **Проверить все endpoints** используя этот checklist
2. **Протестировать с мобильным приложением** используя MOBILE_INTEGRATION.md
3. **Настроить мониторинг** и алерты
4. **Документировать** любые модификации архитектуры
5. **Регулярно обновлять** зависимости

---

**Готово к тестированию!** 🎉
