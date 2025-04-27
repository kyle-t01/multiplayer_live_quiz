FROM amazoncorretto:21 AS build
WORKDIR /backend
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY . .
RUN ./gradlew build --no-daemon

FROM amazoncorretto:21-runtime AS run
WORKDIR /app
COPY --from=build /app/build/libs/multiplayerquizgame-0.0.1-SNAPSHOT.jar backend.jar
EXPOSE 8080

CMD ["java", "-jar","backend.jar"]