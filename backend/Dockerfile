FROM amazoncorretto:21
WORKDIR /backend
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts settings.gradle.kts ./
COPY . .

RUN ./gradlew build --no-daemon

EXPOSE 8080

CMD ["java", "-jar","build/libs/multiplayerquizgame-0.0.1-SNAPSHOT.jar"]