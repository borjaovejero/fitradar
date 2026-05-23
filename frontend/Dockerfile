FROM ghcr.io/jqlang/jq:latest AS jq-stage

FROM eclipse-temurin:21-jdk AS build
COPY --from=jq-stage /jq /usr/bin/jq

ENV HOME=/app
RUN mkdir -p $HOME
WORKDIR $HOME
COPY . $HOME

RUN chmod +x mvnw && ./mvnw clean package -Pproduction -DskipTests

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-XX:+UseSerialGC", "-Xmx256m", "-Dvaadin.productionMode=true", "-jar", "/app.jar"]