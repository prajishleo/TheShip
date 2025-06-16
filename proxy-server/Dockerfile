FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/proxy-server-1.0-SNAPSHOT-jar-with-dependencies.jar proxy-server.jar
CMD ["java", "-jar", "proxy-server.jar"]