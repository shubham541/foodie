# Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B -DskipTests package

# Run
FROM eclipse-temurin:21-jre
RUN useradd --system --create-home foodie
WORKDIR /app
COPY --from=build /app/target/foodie-1.0.0.jar app.jar
USER foodie
EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
