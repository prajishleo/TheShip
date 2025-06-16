FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/proxy-client-1.0-SNAPSHOT-jar-with-dependencies.jar proxy-client.jar
CMD ["java", "-jar", "proxy-client.jar"]