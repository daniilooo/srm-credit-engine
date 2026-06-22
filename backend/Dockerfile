FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

COPY pom.xml ./
RUN mvn dependency:go-offline -q --no-transfer-progress

COPY src/ src/
RUN mvn clean package -DskipTests -q --no-transfer-progress

FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

RUN apk add --no-cache wget

COPY --from=builder /app/target/*.jar app.jar
COPY docker-entrypoint.sh ./
RUN chmod +x docker-entrypoint.sh

EXPOSE 8080
ENTRYPOINT ["./docker-entrypoint.sh"]
