FROM openjdk:21
LABEL authors="Dywinar"
ARG APP_JAR=*.jar
WORKDIR /feed
COPY ${APP_JAR} feed

ENTRYPOINT ["java", "-jar", "feed"]