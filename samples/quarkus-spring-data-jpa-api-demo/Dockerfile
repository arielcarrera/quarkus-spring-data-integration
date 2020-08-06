FROM openjdk:11-jdk-slim
ARG JAR_FILE=target/*-runner.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]