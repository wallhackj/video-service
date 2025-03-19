FROM eclipse-temurin:21-jdk
ARG JAR_FILE=build/libs/demo*.jar
COPY ${JAR_FILE} /tmp/app.jar
LABEL authors="omnissiah"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/tmp/app.jar"]