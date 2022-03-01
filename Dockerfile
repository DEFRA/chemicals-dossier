FROM defradigital/java:latest-jre

ARG BUILD_VERSION

USER root

RUN mkdir -p /usr/src/reach-dossier
WORKDIR /usr/src/reach-dossier

COPY ./target/reach-dossier-${BUILD_VERSION}.jar /usr/src/reach-dossier/reach-dossier.jar
COPY ./target/agent/applicationinsights-agent.jar /usr/src/reach-dossier/applicationinsights-agent.jar
COPY ./target/classes/applicationinsights.json /usr/src/reach-dossier/applicationinsights.json

RUN chown jreuser /usr/src/reach-dossier
USER jreuser

EXPOSE 8097

CMD java -javaagent:/usr/src/reach-dossier/applicationinsights-agent.jar \
-Xmx${JAVA_MX:-2048M} -Xms${JAVA_MS:-2048M} -jar reach-dossier.jar
