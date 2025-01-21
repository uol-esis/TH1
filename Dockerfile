# Phase 1: Build
FROM openjdk:21-oracle

# Create and copy app files
RUN mkdir /app
COPY . /app

# Set work directory
WORKDIR /app

# Ensure Maven wrapper is executable
RUN chmod +x ./mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Phase 2: Runtime
FROM openjdk:21-oracle

# Copy the JAR fromRequest the build stage
COPY --from=build /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
