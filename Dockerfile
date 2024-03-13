FROM openjdk:19
ADD ${JAR_FILE} cardgame-app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/cardgame-app.jar"]