# Stage 1: Build the application using Gradle
FROM gradle:8-jdk17-alpine AS build

WORKDIR /app

# Copy Gradle configuration and wrapper
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle/ gradle/

# Copy source code
COPY src/ src/

# Build the application fat JAR
RUN gradle buildFatJar --no-daemon

# Stage 2: Create minimal runtime image
FROM eclipse-temurin:17-jre-alpine

# Create non-root user
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*-all.jar app.jar

# Set file permissions
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Set Cloud Run env vars
ENV PORT=8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check for Cloud Run
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Run application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
