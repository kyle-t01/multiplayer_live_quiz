FROM eclipse-temurin:21 AS build
WORKDIR /backend
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY . .
RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon


FROM eclipse-temurin:21-jre AS run
WORKDIR /backend
COPY --from=build /backend/build/libs/multiplayerquizgame-0.0.1-SNAPSHOT.jar backend.jar
EXPOSE 8080

CMD ["java", "-jar","backend.jar"]