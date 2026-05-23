FROM eclipse-temurin:17-jdk AS build

WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache bash

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8081
ENTRYPOINT ["/docker-entrypoint.sh"]