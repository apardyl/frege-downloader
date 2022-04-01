FROM adoptopenjdk/openjdk11:latest
RUN ./gradlew build
COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
