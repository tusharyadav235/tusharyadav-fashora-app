# ══════════════════════════════════════════════════════════════════════════════
#  Fashora Backend — Multi-stage Dockerfile
#  Stage 1 : Maven build  (maven:3.9-eclipse-temurin-17)
#  Stage 2 : Runtime      (eclipse-temurin:17-jre  — multi-arch amd64/arm64)
# ══════════════════════════════════════════════════════════════════════════════

# ── Stage 1 : Build ───────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Cache Maven dependencies separately from source code
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and compile
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2 : Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre

LABEL maintainer="fashora-team"
LABEL app="fashora-backend"

WORKDIR /app

# Non-root user
RUN groupadd -r fashora && useradd -r -g fashora -s /bin/false fashora

COPY --from=builder /app/target/*.jar app.jar
RUN chown fashora:fashora app.jar

USER fashora

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=90s --retries=5 \
  CMD curl -fs http://localhost:8080/actuator/health | grep -q UP || exit 1

ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
