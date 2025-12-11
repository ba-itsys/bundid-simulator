# Use an official Maven image as the base image
FROM maven:3.9.8-eclipse-temurin-21 AS build
# Set the working directory in the container
WORKDIR /app
# Copy the pom.xml and the project files to the container
COPY pom.xml .
COPY src ./src
# Build the application using Maven
RUN mvn clean package


# Use an official OpenJDK image as the base image
FROM eclipse-temurin:21-jre
# Set the working directory in the container
WORKDIR /app
# Copy the built JAR file from the previous stage to the container
COPY --from=build /app/target/bundid-simulator.jar .
# Set the command to run the application
CMD ["java", "-jar", "bundid-simulator.jar"]
