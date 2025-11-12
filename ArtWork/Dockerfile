FROM openjdk:17-jdk-slim

ARG jarToCopy

#COPY target/${jarToCopy} /app/app.jar
COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
