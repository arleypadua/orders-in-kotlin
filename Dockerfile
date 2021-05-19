FROM openjdk:14-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew wrapper
RUN ./gradlew bootJar

FROM openjdk:11-jre-slim AS app
EXPOSE 8080
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "./app.jar"]