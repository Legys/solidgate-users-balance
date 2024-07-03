FROM gradle:8.8.0-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY src ./src

RUN gradle clean build -x test

FROM amazoncorretto:21
WORKDIR /app
COPY --from=build /app/build/libs/solidgate-balance-update-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]