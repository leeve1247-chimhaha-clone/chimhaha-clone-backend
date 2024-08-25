# Stage 1: Build the application
FROM gradle:8.10.0-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle bootJar

# Stage 2: Run the application
FROM amazoncorretto:21
WORKDIR /app
COPY . .
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]


## Stage 1-1: 한번에 빌드
#FROM amazoncorretto:21
#WORKDIR /app
#COPY . .
#RUN chmod +x gradlew
#RUN ./gradlew bootJar
#RUN ls -al build/libs
#COPY ./build/libs/*.jar app.jar
#EXPOSE 8080
#CMD ["java", "-jar", "app.jar"]