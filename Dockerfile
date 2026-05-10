FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Кэшируем зависимости отдельно от исходников
COPY pom.xml .
COPY common/pom.xml ./common/
COPY product-service/pom.xml ./product-service/
COPY inventory-service/pom.xml ./inventory-service/
COPY order-service/pom.xml ./order-service/
COPY payment-service/pom.xml ./payment-service/

RUN mvn dependency:go-offline -B

# Копируем исходники и собираем
COPY common/src ./common/src
COPY product-service/src ./product-service/src
COPY inventory-service/src ./inventory-service/src
COPY order-service/src ./order-service/src
COPY payment-service/src ./payment-service/src

RUN mvn clean package -DskipTests -B

# ─── Runtime образы ───────────────────────────────────────────────

FROM eclipse-temurin:21-jre-alpine AS product-service
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/product-service/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS inventory-service
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/inventory-service/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS order-service
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/order-service/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

FROM eclipse-temurin:21-jre-alpine AS payment-service
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=build /app/payment-service/target/*.jar app.jar
USER appuser
EXPOSE 8080
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]