# For Spring Boot application (Dockerfile in Spring Boot project)
FROM openjdk:21-jdk-slim
VOLUME /tmp
COPY target/pubmed-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
