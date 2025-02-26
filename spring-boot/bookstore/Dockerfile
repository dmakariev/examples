FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:21 AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw install -DskipTests -Penhance
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:21
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.makariev.examples.spring.bookstore.BookstoreApplication"]