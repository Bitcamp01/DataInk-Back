FROM openjdk:17-jdk-alpine AS build

WORKDIR /app

ARG JAR_FILE=./dataink-back/build/libs/dataink-back-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} /app/app.jar

FROM nginx:alpine

COPY nginx.conf /etc/nginx/nginx.conf

COPY --from=build /app/app.jar /app/app.jar

RUN apk add --no-cache openjdk17-jre supervisor

COPY supervisord.conf /etc/supervisord.conf

EXPOSE 9090 80 443

CMD ["/usr/bin/supervisord", "-c", "/etc/supervisord.conf"]