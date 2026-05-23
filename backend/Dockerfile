FROM eclipse-temurin:17-jdk AS build

WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache bash

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

CMD ["sh", "-c", "exec java -XX:+UseSerialGC -Xmx256m -jar /app/app.jar"]