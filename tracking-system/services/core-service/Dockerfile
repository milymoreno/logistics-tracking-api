FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY HelloWorld.java .

RUN javac HelloWorld.java && adduser -D appuser
USER appuser

EXPOSE 8080

CMD ["java", "HelloWorld"]