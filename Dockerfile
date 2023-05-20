FROM openjdk:11.0.11-jdk

COPY ./java-test-bot /app

CMD ["java", "-jar", "myapp.jar"]