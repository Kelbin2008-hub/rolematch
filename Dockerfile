# ==============================================================================
# RoleMatch - Enterprise Production Dockerfile for Railway & Cloud Containers
# Multi-stage build: Maven build + Eclipse Temurin Java 21 Alpine JRE
# ==============================================================================

# Stage 1: Build JAR using Maven
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# Cache Maven dependencies layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and package executable fat JAR
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Ultra-lightweight JRE Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Run as non-privileged security user
RUN addgroup -S rolematch && adduser -S rolematch -G rolematch
USER rolematch

# Copy compiled JAR from build stage
COPY --from=builder /workspace/target/rolematch-backend-1.0.0-SNAPSHOT.jar app.jar

# Railway dynamic port binding (Railway injects $PORT at runtime)
ENV PORT=8080
EXPOSE 8080

# Production Java performance flags
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
