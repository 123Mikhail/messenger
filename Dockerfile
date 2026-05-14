# Этап сборки (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true -Dcheckstyle.skip=true

# Этап запуска (Run)
FROM eclipse-temurin:21-jre
WORKDIR /app
# Копируем ИМЕННО наш толстый файл, без всяких звездочек!
COPY --from=build /app/target/messenger-app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]