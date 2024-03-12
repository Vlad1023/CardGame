FROM openjdk:19
ARG JAR_FILE=target/cardgame-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} cardgame-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/cardgame-app.jar"]