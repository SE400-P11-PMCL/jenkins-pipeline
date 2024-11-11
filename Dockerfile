FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/cicd-se400.jar .
EXPOSE 8081
ENTRYPOINT ["java","-jar","cicd-se400.jar"]