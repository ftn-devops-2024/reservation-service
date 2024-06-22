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

# Use curl to download the OpenTelemetry Java agent
FROM curlimages/curl:8.2.1 AS download
ARG OTEL_AGENT_VERSION="1.33.2"
RUN curl --silent --fail -L "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_AGENT_VERSION}/opentelemetry-javaagent.jar" \
    -o "$HOME/opentelemetry-javaagent.jar"

# Use a lightweight base image with JRE pre-installed
FROM openjdk:17-slim

# Set the working directory in the container
WORKDIR /app

# Copy the compiled JAR file from the previous stage
COPY --from=build /app/target/*.jar ./app.jar

COPY --from=download /home/curl_user/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar


# Expose the port the application runs on
EXPOSE 9002

# Command to run the application
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-jar", "app.jar"]
