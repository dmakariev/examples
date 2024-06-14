FROM public.ecr.aws/docker/library/amazoncorretto:22-alpine AS build

RUN apk --no-cache add bash
RUN apk --no-cache add curl
RUN mkdir /app
WORKDIR /app
COPY . /app

RUN --mount=type=cache,target=/root/.m2 curl -Ls https://sh.jbang.dev | bash -s - export portable springbootCompare.java

FROM public.ecr.aws/docker/library/amazoncorretto:22-alpine
RUN mkdir /app/
RUN mkdir /app/lib
COPY --from=build /app/springbootCompare.jar /app/springbootCompare.jar
COPY --from=build /app/lib/* /app/lib/
WORKDIR /app

ENTRYPOINT ["java","-jar","springbootCompare.jar"]