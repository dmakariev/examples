FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:25-cds AS build

WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw clean compile spring-boot:process-aot package -DskipTests -Penhance

FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:25-cds as optimizer
WORKDIR /workspace/app
COPY --from=build /workspace/app/target/*.jar application.jar

RUN java -Djarmode=tools -jar application.jar extract --destination application
WORKDIR /workspace/app/application
RUN java -Dspring.aot.enabled=true -XX:ArchiveClassesAtExit=application.jsa -Dspring.context.exit=onRefresh -jar application.jar

FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:25-cds

VOLUME /tmp

ARG DEPENDENCY=/workspace/app
COPY --from=optimizer ${DEPENDENCY}/application /app/application
COPY --from=optimizer ${DEPENDENCY}/application/application.jsa /app/application.jsa

WORKDIR /app/application

ENTRYPOINT ["java", "-Dspring.aot.enabled=true", "-XX:+UseCompactObjectHeaders", "-XX:SharedArchiveFile=application.jsa", "-jar", "application.jar"]
