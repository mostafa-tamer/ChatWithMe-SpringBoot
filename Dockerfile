# Use an official Maven image to build the application
#FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory inside the container
#WORKDIR /app

# Copy the pom.xml file and download the dependencies
#COPY pom.xml .
#RUN mvn dependency:go-offline

# Copy the source code and build the application
#COPY src ./src
#RUN mvn package -DskipTests

# Use an official OpenJDK runtime as the base image
FROM openjdk:17-jdk-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]
