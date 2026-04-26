# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY sfs-common/pom.xml sfs-common/pom.xml
COPY sfs/pom.xml sfs/pom.xml
RUN mvn dependency:go-offline -B
COPY sfs-common/src sfs-common/src
COPY sfs/src sfs/src
RUN mvn clean package -DskipTests -B

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/sfs/target/*.jar app.jar
EXPOSE 8080
ENV JAVA_OPTS="-XX:+UseZGC -Xms512m -Xmx1g -Dspring.threads.virtual.enabled=true"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"]
