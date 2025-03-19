# Use official OpenJDK image as a base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the target directory (after build) into the container
COPY target/bank-management-0.0.1-SNAPSHOT.jar /app/bank-management-0.0.1-SNAPSHOT.jar

# Expose the port your Spring Boot app runs on (default is 8080)
EXPOSE 8081

# Command to run your Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/bank-management-0.0.1-SNAPSHOT.jar"]
