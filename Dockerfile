# Stage 1: Build the Ktor application using Gradle
FROM gradle:8.8-jdk21-alpine AS builder

# Set the working directory
WORKDIR /home/gradle/project

# Copy gradle wrapper and build scripts first to leverage Docker cache
COPY gradlew gradle.properties settings.gradle.kts ./
COPY gradle/ ./gradle/
COPY build.gradle.kts ./
COPY backend/build.gradle.kts ./backend/

# Grant executable permissions to the Gradle wrapper
RUN chmod +x ./gradlew

# Copy the backend source code
COPY backend/src/ ./backend/src/

# Build the shadow JAR (fat JAR) for the backend module
# The --no-daemon flag is recommended for CI/CD environments
RUN ./gradlew :backend:shadowJar --no-daemon

# Stage 2: Create the final lightweight image
# Using Eclipse Temurin JRE for a smaller footprint
FROM eclipse-temurin:21-jre-alpine

# Set the working directory
WORKDIR /app

# Copy the built fat JAR from the builder stage.
# The JAR is typically named 'backend-all.jar' by the shadow plugin.
COPY --from=builder /home/gradle/project/backend/build/libs/backend-all.jar ./application.jar

# Expose the port the application will run on
EXPOSE 5070

# Command to run the application
ENTRYPOINT ["java", "-jar", "application.jar"]
