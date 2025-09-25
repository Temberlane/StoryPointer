# syntax=docker/dockerfile:1
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
COPY model ./model
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/target/storypointer-backend-0.1.0.jar /app/app.jar
COPY model/model.onnx /app/model/model.onnx
EXPOSE 8080
ENTRYPOINT ["java","-Xms512m","-Xmx2g","-jar","/app/app.jar"]
