FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:21-cds as build

WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw install -DskipTests

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

RUN java -Djarmode=tools -jar application.jar extract --destination application
WORKDIR /workspace/app/application
RUN java -XX:ArchiveClassesAtExit=application.jsa -Dspring.context.exit=onRefresh -jar application.jar

FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:21-cds

VOLUME /tmp

ARG DEPENDENCY=/workspace/app
COPY --from=build ${DEPENDENCY}/application /app/application
COPY --from=build ${DEPENDENCY}/application/application.jsa /app/application.jsa

WORKDIR /app/application

ENTRYPOINT ["java", "-XX:SharedArchiveFile=application.jsa", "-jar", "application.jar"]
