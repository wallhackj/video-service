FROM eclipse-temurin:21-jdk
ARG JAR_FILE=target/anewprojex*.jar
COPY ${JAR_FILE} /tmp/app.jar
LABEL authors="omnissiah"
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/tmp/app.jar"]