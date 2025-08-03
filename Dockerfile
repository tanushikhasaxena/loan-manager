# Use the official OpenJDK image as a base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper and project files
COPY pom.xml .
COPY src ./src
COPY mvnw .
COPY .mvn ./.mvn

# Grant execute permission to the Maven wrapper script
RUN chmod +x ./mvnw

# Package the application using Maven to a JAR file
RUN ./mvnw clean package -DskipTests

# The JAR file is created in the target directory
# Set the entry point to run the JAR file
ENTRYPOINT ["java", "-jar", "/app/target/loan-management-0.0.1-SNAPSHOT.jar"]