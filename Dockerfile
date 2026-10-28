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
# Aliases are found by certificate subject, so this works whatever the alias names are.
RUN keytool -list -v -cacerts -storepass changeit \
    | awk '/^Alias name:/ {sub(/^Alias name: /, ""); a=$0} /^Owner:/ && /emSign|AAA Certificate Services/ {print a}' \
    | sort -u \
    | while IFS= read -r alias; do \
        echo "Removing trust anchor: $alias"; \
        keytool -delete -cacerts -storepass changeit -alias "$alias"; \
      done

# Verification: number of eMudhra/AAA certificates still trusted (expected: 0)
RUN echo "REMAINING_ROOTS=$(keytool -list -v -cacerts -storepass changeit | grep '^Owner:' | grep -ciE 'emSign|AAA Certificate Services')"

RUN addgroup -S spring && adduser -S spring -G spring
COPY --from=build /app/target/portfolio.jar app.jar
USER spring

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+UseSerialGC"

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]