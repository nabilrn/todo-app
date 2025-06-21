# Use a multi-stage build to reduce final image size
FROM gradle:8-jdk17-alpine AS build

# Set working directory
WORKDIR /app

# Copy gradle files first for better caching
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle/ gradle/

# Copy source code
COPY src src/

# Build the application
RUN gradle buildFatJar --no-daemon

# Production stage
FROM openjdk:17-jre-alpine

# Create a non-root user for security
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Set working directory
WORKDIR /app

# Copy the built JAR from the build stage
COPY --from=build /app/build/libs/*-all.jar app.jar

# Change ownership of the app directory
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose the port your app runs on
EXPOSE 8080

# Set environment variables for Cloud Run
ENV PORT=8080
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check (optional but recommended for Cloud Run)
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/ || exit 1

# Run the application
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]