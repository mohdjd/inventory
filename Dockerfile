# ---- Stage 1: Build the React frontend ----
FROM node:18-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# ---- Stage 2: Build the Spring Boot backend ----
FROM maven:3.9-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY pom.xml ./
COPY src ./src
COPY --from=frontend-build /app/frontend/build ./frontend/build
# Frontend is already built above, so skip the npm install/build steps in Maven
# and let maven-resources-plugin copy the pre-built frontend/build into static resources.
RUN mvn -B clean package -DskipFrontend=true -DskipTests

# ---- Stage 3: Runtime image ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN apk add --no-cache curl
COPY --from=backend-build /app/target/*.jar app.jar

EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]