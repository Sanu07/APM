FROM openjdk:11
EXPOSE 8000
ARG JAR_FILE=target/employee-dashboard.jar
COPY ${JAR_FILE} employee-dashboard.jar
ENTRYPOINT [ "java", "-jar", "/employee-dashboard.jar" ]