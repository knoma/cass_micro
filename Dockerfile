FROM openjdk:14-alpine
COPY build/libs/cass_micro-*-all.jar cass_micro.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "cass_micro.jar"]