FROM openjdk:11-jre-slim
MAINTAINER mkosturkov
COPY target/store-0.0.1-SNAPSHOT.jar store.jar
ENTRYPOINT ["java","-jar","/store.jar"]