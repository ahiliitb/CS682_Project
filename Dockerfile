# syntax=docker/dockerfile:1
#
# Build:  docker build -t ahilkhaniitb/campuscart:latest .
# Run:   docker run --rm -p 8080:8080 ahilkhaniitb/campuscart:latest
# Push:  docker login && docker push ahilkhaniitb/campuscart:latest
# Hub:   https://hub.docker.com/r/ahilkhaniitb/campuscart
#
# With persisted H2 + uploads: docker compose up (see docker-compose.yml)

FROM eclipse-temurin:21-jdk AS build
WORKDIR /build

COPY pom.xml .
COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
RUN chmod +x mvnw

COPY src src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app --home /app app

COPY --from=build /build/target/*.jar app.jar
RUN chown -R app:app /app

USER app:app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
