FROM openjdk:8-jdk-alpine
 

ENV PINPOINT_VERSION=1.7.3

ENV COLLECTOR_IP=10.8.101.186

ADD files/configure-agent.sh /usr/local/bin/


RUN apk add --update curl bash \
    && chmod a+x /usr/local/bin/configure-agent.sh \
    && mkdir -p /assets/pinpoint-agent \
    && curl -SL https://raw.githubusercontent.com/naver/pinpoint/$PINPOINT_VERSION/agent/src/main/resources-release/pinpoint.config -o /assets/pinpoint.config \
    && curl -SL https://github.com/naver/pinpoint/releases/download/$PINPOINT_VERSION/pinpoint-agent-$PINPOINT_VERSION.tar.gz -o pinpoint-agent-$PINPOINT_VERSION.tar.gz \
    && gunzip pinpoint-agent-$PINPOINT_VERSION.tar.gz \
    && tar -xf pinpoint-agent-$PINPOINT_VERSION.tar -C /assets/pinpoint-agent \
    && curl -SL https://raw.githubusercontent.com/naver/pinpoint/$PINPOINT_VERSION/agent/src/main/resources-release/lib/log4j.xml -o /assets/pinpoint-agent/lib/log4j.xml \
    && sed -i 's/DEBUG/INFO/' /assets/pinpoint-agent/lib/log4j.xml \
    && rm pinpoint-agent-$PINPOINT_VERSION.tar \
    && apk del curl \
    && rm /var/cache/apk/*


ADD build/libs/*.jar app.jar

RUN /usr/local/bin/configure-agent.sh


ENTRYPOINT [ "java","-javaagent:/assets/pinpoint-agent/pinpoint-bootstrap-1.7.3.jar" ,"-Dpinpoint.agentId=userapi", "-Dpinpoint.applicationName=userapi", "-jar", "app.jar"]

EXPOSE 8180
