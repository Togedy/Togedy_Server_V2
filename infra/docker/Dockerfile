# Build stage

FROM bellsoft/liberica-openjdk-alpine:17 AS builder

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY settings.gradle* build.gradle* ./

RUN chmod +x gradlew && \
    ./gradlew --no-daemon dependencies

COPY . .

RUN chmod +x gradlew && \
    ./gradlew --no-daemon clean bootJar -x test


# Run stage

FROM bellsoft/liberica-openjdk-alpine:17

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]