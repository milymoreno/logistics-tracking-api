# Dockerfile para microservicio Quarkus (modo JVM)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia el jar generado por Quarkus
COPY target/quarkus-app/lib/ /app/lib/
COPY target/quarkus-app/*.jar /app/
COPY target/quarkus-app/app/ /app/app/
COPY target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8081

ENV JAVA_OPTS="-Dquarkus.http.port=8081"

CMD ["java", "-jar", "quarkus-run.jar"]
