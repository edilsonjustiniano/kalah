FROM openjdk:8
MAINTAINER edilsonjustiniano
COPY kalah-1.0.0.jar /home/
EXPOSE 8080
CMD ["java", "-jar", "/home/kalah-1.0.0.jar", "--spring.data.mongodb.uri=mongodb://mongodb:27017"]