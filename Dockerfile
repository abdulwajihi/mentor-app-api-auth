# 1. Use the base image with Java installed
FROM openjdk:22-jdk-slim

# 2. Set the working directory inside the container
WORKDIR /app

# 3. Copy the JAR file into the container
COPY target/authapi-0.0.1-SNAPSHOT.jar app.jar

# 4. Expose the port your app runs on (default is 8080)
EXPOSE 8080

# 5. Define the command to run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
