# Stage 1: Build the application
FROM gradle:8.10.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar

# Stage 2: Run the application
FROM amazoncorretto:21
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]