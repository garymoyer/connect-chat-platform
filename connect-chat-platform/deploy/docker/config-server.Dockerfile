# Build context must be the repo root: docker build -f deploy/docker/config-server.Dockerfile .

FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY config-server/pom.xml config-server/pom.xml
COPY platform-service/pom.xml platform-service/pom.xml
RUN mvn -B -ntp -pl config-server -am dependency:go-offline
COPY config-server/src config-server/src
COPY platform-service/src platform-service/src
RUN mvn -B -ntp -pl config-server -am package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --create-home appuser
COPY --from=build /workspace/config-server/target/config-server-*.jar app.jar
USER appuser
EXPOSE 8888
ENTRYPOINT ["java", "-jar", "app.jar"]
