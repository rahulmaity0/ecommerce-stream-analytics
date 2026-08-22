FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY producer-service/pom.xml producer-service/pom.xml
COPY consumer-service/pom.xml consumer-service/pom.xml
COPY consumer-service/src consumer-service/src

RUN mvn -pl consumer-service -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/consumer-service/target/consumer-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
