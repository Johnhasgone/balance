FROM maven:3.8.6-eclipse-temurin-17-alpine
WORKDIR /balance
COPY ./balance/pom.xml .
RUN mvn dependency:resolve

COPY ./balance/src ./src
RUN mvn package -DskipTests

FROM openjdk:17-alpine
WORKDIR /balance
COPY --from=0 /balance/target/balance-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "balance-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080