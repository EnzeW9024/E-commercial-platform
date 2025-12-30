#
# Multi-stage Dockerfile for E-Commerce Spring Boot app
#

# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Pre-copy pom to leverage Docker layer caching
COPY pom.xml pom.xml

# Download dependencies (go-offline) to cache
RUN mvn -q -B -e -DskipTests dependency:go-offline

# Now copy sources and build
COPY src/ src/
RUN mvn -q -B -DskipTests package

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy built artifact
COPY --from=build /workspace/target/*.jar /app/app.jar

# Container port
EXPOSE 8081

# JVM options can be injected via JAVA_OPTS env var
ENV JAVA_OPTS=""

# Spring Boot recognizes env overrides for application.yml
# e.g., SPRING_DATASOURCE_URL, SPRING_REDIS_HOST, KAFKA_BOOTSTRAP_SERVERS, SERVER_PORT
ENTRYPOINT ["bash","-lc","java $JAVA_OPTS -jar /app/app.jar"]


