# syntax=docker/dockerfile:experimental
FROM eclipse-temurin:19-jdk-alpine AS build_Stage
ENV APP_HOME=/usr/app/
COPY . $APP_HOME
WORKDIR $APP_HOME
USER root
RUN pwd
RUN echo "output"
RUN ls -alh
#RUN --mount=type=cache,mode=0777,target=/root/.gradle ./gradlew clean build
RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon -i  clean build
RUN mkdir -p build/dependency && (cd build/dependency;)
#jar -xf ../libs/*-SNAPSHOT.jar)

FROM eclipse-temurin:19-jdk-alpine
ENV APP_HOME=/usr/app
ENV ARTIFACT_NAME=*-SNAPSHOT.jar
WORKDIR $APP_HOME
USER root
COPY --from=build_Stage $APP_HOME/build/libs/$ARTIFACT_NAME .

ENV JAVA_OPTS='-Dspring.application.name=priceengine \
-XX:+UseContainerSupport \
-XX:MaxRAMPercentage=75.0 \
-XX:+UseSerialGC \
-XX:+AlwaysPreTouch \
-XX:+PerfDisableSharedMem \
-XX:-OmitStackTraceInFastThrow \
-Djava.security.egd=file:/dev/./urandom \
-Dspring.profiles.active=no_cronjob'
ENTRYPOINT exec java $JAVA_OPTS -jar ${ARTIFACT_NAME}



#VOLUME /Users/dengdave/Desktop/docker_mount/price_engine
#ARG DEPENDENCY=$APP_HOME/build/dependency
#COPY --from=build ${DEPENDENCY}//BOOT-INF/lib $APP_HOME/lib
#COPY --from=build ${DEPENDENCY}/META-INF $APP_HOME/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes $APP_HOME
#RUN chmod -R 777 $APP_HOME
#ENV JAVA_OPTS='-Dspring.application.name=priceengine \
#-XX:+UseContainerSupport \
#-XX:MaxRAMPercentage=75.0 \
#-XX:+UseSerialGC \
#-XX:+AlwaysPreTouch \
#-XX:+PerfDisableSharedMem \
#-XX:-OmitStackTraceInFastThrow \
#-Djava.security.egd=file:/dev/./urandom \
#-Dspring.profiles.active=no_cronjob'
#ENTRYPOINT ["tail", "-f", "/dev/null"]
#ENTRYPOINT ["java","-cp",".;./lib/*","com.backend.stock.priceengine.PriceEngineApplication"]
