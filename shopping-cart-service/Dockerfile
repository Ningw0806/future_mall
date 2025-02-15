# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the pre-built JAR file into the container
COPY target/shopping-cart-service-*.jar shopping-cart-service.jar

# Expose the port the application runs on
EXPOSE 8085

# Run the application
ENTRYPOINT ["java", "-jar", "shopping-cart-service.jar"]