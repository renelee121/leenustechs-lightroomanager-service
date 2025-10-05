# Etapa 1: Build con Maven (Amazon Corretto 17)
FROM maven:3.9.11-amazoncorretto-17-alpine AS build

WORKDIR /app

# Copiar pom.xml para cachear dependencias
COPY pom.xml .

RUN mvn dependency:go-offline

# Copiar el código fuente
COPY src ./src

# Compilar y empaquetar el proyecto
RUN mvn clean package -DskipTests

# Etapa 2: Runtime con Java 17 Alpine
FROM openjdk:17-alpine

WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Definir variables de entorno para Spring Boot
ARG KAFKA_BOOTSTRAP_ADDRESS
ARG KAFKA_TOPIC_NAME
ARG SYNC_MANAGER_URL

ENV KAFKA_BOOTSTRAP_ADDRESS=${KAFKA_BOOTSTRAP_ADDRESS}
ENV KAFKA_TOPIC_NAME=${KAFKA_TOPIC_NAME}
ENV SYNC_MANAGER_URL=${SYNC_MANAGER_URL}

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "app.jar"]