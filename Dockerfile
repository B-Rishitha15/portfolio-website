# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Security: remove the compromised eMudhra (emSign) roots and the Sectigo AAA cross-signer
# root from Java's trust store (eMudhra CA compromise, Oct 2026).
RUN keytool -list -cacerts -storepass changeit \
    | grep -iE '^(emsign|comodoaaa|aaacert|aaa certificate)' \
    | cut -d, -f1 \
    | while IFS= read -r alias; do \
        echo "Removing trust anchor: $alias"; \
        keytool -delete -cacerts -storepass changeit -alias "$alias"; \
      done

# Verification: prints how many of these certificates are still in the trust store (expected: 0)
RUN echo "Remaining eMudhra/AAA roots: $(keytool -list -v -cacerts -storepass changeit | grep -ciE 'emSign|AAA Certificate Services')"

RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/portfolio.jar app.jar
USER spring

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]