# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the pre-built JAR file into the container
COPY target/future-common-*.jar future-common.jar

# Run the application
ENTRYPOINT ["java", "-jar", "future-common.jar"]