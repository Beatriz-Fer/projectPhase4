# base image
FROM openjdk:19

# working directory
WORKDIR /app

# Spring Boot JAR file
COPY target/twitter-clone-project-0.0.1-SNAPSHOT.jar /app/app.jar

# port
EXPOSE 8080

# command to run application
CMD ["java", "-jar", "app.jar"]