# Use a lighter base image to run the built application
FROM eclipse-temurin:17-jre

# Copy the built JAR file from the builder stage
# COPY --from=builder /app/target/*.jar app.jar
COPY target/*.jar app.jar

# Expose the port on which your app runs (e.g., 8080)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]

