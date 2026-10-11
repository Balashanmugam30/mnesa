# Multi-stage Dockerfile for MNESA Spring Boot Backend
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /workspace

# Copy gradle wrapper and dependency declarations
COPY gradlew gradlew.bat settings.gradle build.gradle gradle.properties ./
COPY gradle ./gradle

# Make gradlew executable
RUN chmod +x gradlew

# Download dependencies (cache layer)
RUN ./gradlew dependencies --no-daemon || true

# Copy source code and build bootJar
COPY src ./src
RUN ./gradlew bootJar -x test --no-daemon

# Production/Staging runtime image
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add unprivileged user for security
RUN addgroup -S mnesa && adduser -S mnesa -G mnesa
USER mnesa:mnesa

COPY --from=builder /workspace/build/libs/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --retries=3 \
  CMD wget -q --spider http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "exec java -Djava.security.egd=file:/dev/./urandom -Dserver.port=${PORT:-8080} -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-staging} -jar app.jar"]
