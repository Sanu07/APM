FROM openjdk:11
EXPOSE 9000
ARG JAR_FILE=target/employee-service.jar
COPY ${JAR_FILE} employee-service.jar
ENTRYPOINT [ "java", "-jar", "/employee-service.jar" ]