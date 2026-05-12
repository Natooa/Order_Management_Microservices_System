# syntax=docker/dockerfile:1.4
# ─── Build Stage ─────────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Кэшируем зависимости (слой инвалидируется только при изменении pom.xml)
COPY pom.xml .
COPY common/pom.xml             ./common/
COPY product-service/pom.xml    ./product-service/
COPY inventory-service/pom.xml  ./inventory-service/
COPY order-service/pom.xml      ./order-service/
COPY payment-service/pom.xml    ./payment-service/
COPY shipping-service/pom.xml   ./shipping-service/

# Кэш Maven-репозитория между билдами (~/.m2)
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B --no-transfer-progress

# Копируем исходники
COPY common/src             ./common/src
COPY product-service/src    ./product-service/src
COPY inventory-service/src  ./inventory-service/src
COPY order-service/src      ./order-service/src
COPY payment-service/src    ./payment-service/src
COPY shipping-service/src   ./shipping-service/src

# Собираем всё одним слоем, кэш Maven переиспользуется
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests -B --no-transfer-progress -T 1C

# ─── Base Runtime ─────────────────────────────────────────────────────────────
# Общий базовый образ — создаём пользователя один раз
FROM eclipse-temurin:21-jre-alpine AS base-runtime
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# ─── product-service ─────────────────────────────────────────────────────────
FROM base-runtime AS product-service
COPY --from=build /app/product-service/target/*.jar app.jar
USER appuser
EXPOSE 8081 9002
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]

# ─── inventory-service ───────────────────────────────────────────────────────
FROM base-runtime AS inventory-service
COPY --from=build /app/inventory-service/target/*.jar app.jar
USER appuser
EXPOSE 8082 9001
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]

# ─── order-service ───────────────────────────────────────────────────────────
FROM base-runtime AS order-service
COPY --from=build /app/order-service/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]

# ─── payment-service ─────────────────────────────────────────────────────────
FROM base-runtime AS payment-service
COPY --from=build /app/payment-service/target/*.jar app.jar
USER appuser
EXPOSE 8084
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]

# ─── shipping-service ────────────────────────────────────────────────────────
FROM base-runtime AS shipping-service
COPY --from=build /app/shipping-service/target/*.jar app.jar
USER appuser
EXPOSE 8085
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]