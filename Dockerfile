FROM gradle:7.5.1-jdk11-alphine

COPY . .

RUN gradle build

EXPOSE 8080

ENTRYPOINT  ["java", "-jar", "build/libs/Homebanking-0.0.1-SNAPSHOT.jar"]