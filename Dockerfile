# Use a base image with Maven and JDK pre-installed
FROM maven:3.8.4-openjdk-17-slim AS build

# Set the working directory in the container
WORKDIR /app

# Copy the Maven project definition file
COPY reservation-service/pom.xml .

# Copy the source code
COPY reservation-service/src ./src

# Build the application
RUN mvn clean package -DskipTests
RUN ls

# Use a lightweight base image with JRE pre-installed
FROM openjdk:17-slim

# Set the working directory in the container
WORKDIR /app

# Copy the compiled JAR file from the previous stage
COPY --from=build /app/target/*.jar ./app.jar


# Expose the port the application runs on
EXPOSE 9002

# Command to run the application
CMD ["java", "-jar", "app.jar"]
