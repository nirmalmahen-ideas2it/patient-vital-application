# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the application's JAR file to the container
ARG JAR_FILE=target/patient-vital-application.jar
COPY ${JAR_FILE} app.jar

# Expose the port the application runs on
EXPOSE 9092

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
