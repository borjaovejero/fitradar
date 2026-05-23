FROM eclipse-temurin:17-jdk AS build

WORKDIR /app
COPY . .
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
RUN apk add --no-cache bash

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

CMD ["sh", "-c", "JDBC_URL=$(echo $USERS_DB_URL | sed 's|^postgresql://|jdbc:postgresql://|;s|^postgres://|jdbc:postgresql://|') && exec java -jar /app/app.jar --spring.datasource.url=$JDBC_URL"]