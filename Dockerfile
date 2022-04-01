FROM adoptopenjdk/openjdk11:latest AS build
COPY . /app
WORKDIR /app/
RUN ["./gradlew", "build"]

FROM adoptopenjdk/openjdk11:latest
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
