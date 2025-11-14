# Multi-stage build for Spring Boot application

# Stage 1: Build stage
FROM gradle:8.5-jdk17-alpine AS build

WORKDIR /app

# Copy Gradle wrapper and build files (cached layer)
COPY gradle gradle
COPY gradlew .
COPY gradlew.bat .
COPY settings.gradle.kts .
COPY build.gradle.kts .

# Set execute permission and download dependencies (cached layer)
RUN chmod +x ./gradlew && ./gradlew dependencies --no-daemon

# Copy source code and build
COPY src ./src
RUN ./gradlew bootJar --no-daemon

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
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
