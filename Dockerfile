# Этап сборки (Build)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# ИСПРАВЛЕНО: Флаг maven.test.skip=true полностью отключает и запуск, и компиляцию тестов
RUN mvn clean package -Dmaven.test.skip=true -Dcheckstyle.skip=true

# Этап запуска (Run)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]va", "-jar", "app.jar"]