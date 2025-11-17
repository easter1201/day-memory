# Multi-stage build for Spring Boot application

# Stage 1: Build stage
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Copy Gradle wrapper and build files (cached layer)
COPY gradlew gradlew.bat ./
COPY gradle ./gradle
COPY settings.gradle.kts build.gradle.kts ./

# Fix line endings and set execute permission, then download dependencies
RUN sed -i 's/\r$//' gradlew && \
    chmod +x gradlew && \
    ./gradlew dependencies --no-daemon

# Copy source code and build
COPY src ./src
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Create non-root user for security
RUN groupadd -r spring && useradd -r -g spring spring && \
    mkdir -p /app/logs && \
    chown -R spring:spring /app/logs
USER spring:spring

# Copy JAR from build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
