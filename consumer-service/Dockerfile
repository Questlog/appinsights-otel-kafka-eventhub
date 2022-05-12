FROM eclipse-temurin:17-jre-alpine

COPY target/consumer-service.jar ./consumer-service.jar
COPY target/libs/applicationinsights-agent.jar ./applicationinsights-agent.jar
COPY applicationinsights.json ./applicationinsights.json

EXPOSE 8080

ENTRYPOINT ["java","-javaagent:./applicationinsights-agent.jar","-jar","./consumer-service.jar"]