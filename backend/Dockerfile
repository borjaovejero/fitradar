FROM eclipse-temurin:17-jdk AS build

WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache bash

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

CMD ["sh", "-c", "exec java -XX:+UseSerialGC -Xmx256m -jar /app/app.jar --spring.datasource.url=jdbc:postgresql://dpg-d88v2nbbc2fs73em70tg-a/fitradar --spring.datasource.username=fitradar_user --spring.datasource.password=JRdUc89qpPCTR6Hv9qHAxJ69Oh0zsvAP"]