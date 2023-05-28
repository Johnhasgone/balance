FROM maven:3.8.6-eclipse-temurin-17-alpine
WORKDIR /client
COPY ./client/pom.xml .
RUN mvn dependency:resolve

COPY ./client/src ./src
RUN mvn package -DskipTests

FROM openjdk:17-alpine
WORKDIR /client
COPY --from=0 /client/target/client-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "client-0.0.1-SNAPSHOT.jar"]
EXPOSE 8082