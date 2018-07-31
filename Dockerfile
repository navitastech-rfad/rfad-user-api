FROM openjdk:8-jdk-alpine
 
ADD build/libs/*.jar app.jar

EXPOSE 8180

CMD ["java", "-jar", "app.jar"]


 
