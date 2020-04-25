FROM openjdk:11

LABEL maintainer="gktgnjftm@naver.com"

VOLUME /tmp

EXPOSE 8080

ARG JAR_FILE=target/goodsleepwell-0.0.1-SNAPSHOT.jar

ADD ${JAR_FILE} docker-springboot.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/docker-springboot.jar"]