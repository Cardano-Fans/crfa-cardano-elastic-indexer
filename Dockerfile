FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon -x test

FROM openjdk:21-jre-slim
COPY --from=build /home/gradle/src/build/quarkus-app/lib/ /deployments/lib/
COPY --from=build /home/gradle/src/build/quarkus-app/*.jar /deployments/
COPY --from=build /home/gradle/src/build/quarkus-app/app/ /deployments/app/
COPY --from=build /home/gradle/src/build/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 1001

ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]