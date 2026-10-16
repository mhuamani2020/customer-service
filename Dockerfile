# syntax=docker/dockerfile:1
# ============================================================================
# customer-service — imagen Docker (Parte II: "cada microservicio en un
# contenedor independiente"). Build multi-etapa. No depende de
# bank-resilience-commons.
#
# El contexto de build es la RAÍZ del proyecto (ver docker-compose.yml):
#   docker build -f customer-service/Dockerfile -t customer-service .
# ============================================================================
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY customer-service/pom.xml customer-service/pom.xml
COPY customer-service/src customer-service/src
RUN mvn -q -B -f customer-service/pom.xml -DskipTests package

# ---------- Etapa de ejecución ----------
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /workspace/customer-service/target/*.jar app.jar
EXPOSE 8081
HEALTHCHECK --interval=10s --timeout=5s --start-period=45s --retries=12 \
  CMD bash -c 'exec 3<>/dev/tcp/127.0.0.1/8081'
ENTRYPOINT ["java","-jar","/app/app.jar"]
