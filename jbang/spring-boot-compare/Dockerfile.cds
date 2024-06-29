FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:22-cds  AS build

RUN apk --no-cache add bash
RUN apk --no-cache add curl
RUN mkdir /app
WORKDIR /app
COPY . /app

RUN --mount=type=cache,target=/root/.m2 curl -Ls https://sh.jbang.dev | bash -s - export portable springbootCompare.java

FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:22-cds as optimizer
RUN mkdir /app/
RUN mkdir /app/lib
COPY --from=build /app/springbootCompare.jar /app/springbootCompare.jar
COPY --from=build /app/lib/* /app/lib/
WORKDIR /app
RUN java -XX:ArchiveClassesAtExit=springbootCompare.jsa -Dspring.context.exit=onRefresh -jar springbootCompare.jar

FROM ghcr.io/bell-sw/liberica-openjdk-alpine-musl:22-cds 
RUN mkdir /app/
RUN mkdir /app/lib
COPY --from=optimizer /app/springbootCompare.jar /app/springbootCompare.jar
COPY --from=optimizer /app/springbootCompare.jsa /app/springbootCompare.jsa
COPY --from=optimizer /app/lib/* /app/lib/
WORKDIR /app

ENTRYPOINT ["java","-XX:SharedArchiveFile=springbootCompare.jsa","-jar","springbootCompare.jar"]