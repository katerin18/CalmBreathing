# ================================
# ЭТАП 1: Сборка проекта
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Копируем pom.xml для кеширования зависимостей
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходники
COPY src ./calm_breath/src

# Собираем JAR
RUN mvn clean package -DskipTests

# ================================
# ЭТАП 2: Создание финального образа
# ================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/target/*.jar app.jar

# Создаем пользователя для безопасности
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Точка входа
ENTRYPOINT ["java", "-jar", "app.jar"]

# Открываем порт (замените на ваш)
EXPOSE 8080