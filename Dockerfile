FROM openjdk:8
EXPOSE 8080
ADD target/MoviesProject-docker.jar MoviesProject-docker.jar
ENTRYPOINT ["java","-jar","/MoviesProject-docker.jar"]