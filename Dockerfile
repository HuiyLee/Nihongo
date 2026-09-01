# Builds the Spring Boot backend for a Docker-based host (e.g. Render).
# Lives at the repo root (not backend/) so a plain `docker build .` from the
# repo root works with no extra "root directory" configuration - it just
# COPYs from the backend/ subdirectory internally.
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies separately from source so a source-only change doesn't
# re-download the whole Maven repo on every build.
COPY backend/pom.xml .
RUN mvn -B dependency:go-offline

COPY backend/src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/japanese-learning.jar app.jar

# Render (and most PaaS hosts) inject PORT at runtime; application.yml falls
# back to SERVER_PORT then 8080, so this image also works for local
# `docker run -p 8080:8080 ...` unchanged.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
