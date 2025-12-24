FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY traget/STFAS.jar app/STFAS.jar
EXPOSE 8080
CMD ["java","-jar","app/STFAS.jar"]