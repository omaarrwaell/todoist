FROM openjdk:25-ea-4-jdk-oraclelinux9
WORKDIR /app
COPY target/board-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]

