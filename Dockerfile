FROM openjdk:8
MAINTAINER edilsonjustiniano
COPY build/libs/kalah-0.0.1-SNAPSHOT.jar kalah.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "kalah.jar"]