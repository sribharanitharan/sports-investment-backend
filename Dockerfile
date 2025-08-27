#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage  
#
FROM eclipse-temurin:17-jdk-slim
COPY --from=build /target/sports-investment-backend.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
