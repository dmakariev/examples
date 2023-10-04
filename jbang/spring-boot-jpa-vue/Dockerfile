FROM public.ecr.aws/docker/library/amazoncorretto:21-alpine AS build

RUN apk --no-cache add bash
RUN apk --no-cache add curl
RUN mkdir /app
WORKDIR /app
COPY . /app

RUN curl -Ls https://sh.jbang.dev | bash -s - export portable springbootJpaVue.java

FROM public.ecr.aws/docker/library/amazoncorretto:21-alpine
RUN mkdir /app/
RUN mkdir /app/lib
COPY --from=build /app/springbootJpaVue.jar /app/springbootJpaVue.jar
COPY --from=build /app/lib/* /app/lib/
WORKDIR /app

ENTRYPOINT ["java","-jar","springbootJpaVue.jar"]