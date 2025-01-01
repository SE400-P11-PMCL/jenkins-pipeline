FROM openjdk:17-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y wget && \
    wget https://artifacts.elastic.co/downloads/beats/filebeat/filebeat-7.17.3-amd64.deb && \
    dpkg -i filebeat-7.17.3-amd64.deb && \
    rm filebeat-7.17.3-amd64.deb
COPY target/cicd-se400.jar cicd-se400.jar
COPY filebeat.yml /etc/filebeat/filebeat.yml
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "filebeat -e & java -jar cicd-se400.jar"]

