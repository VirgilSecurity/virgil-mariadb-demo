FROM mariadb:10.4

# Create MariaDB database
#TODO

# Install Java
ENV JAVA_HOME /opt/jdk
ENV PATH ${PATH}:${JAVA_HOME}/bin

ENV JDK_URL https://github.com/AdoptOpenJDK/openjdk11-binaries/releases/download/jdk-11.0.6%2B10/OpenJDK11U-jdk_x64_linux_hotspot_11.0.6_10.tar.gz
ENV JDK_SHA256 330d19a2eaa07ed02757d7a785a77bab49f5ee710ea03b4ee2fa220ddd0feffc
ENV JDK_FILENAME /tmp/OpenJDK11.tar.gz

RUN mkdir -p $JAVA_HOME && chmod -R 777 /opt
RUN curl $JDK_URL -o $JDK_FILENAME \
    && sha256sum $JDK_FILENAME | grep $JDK_SHA256 \
    && tar -xf $JDK_FILENAME --strip-components=1 -C $JAVA_HOME \
    && rm $JDK_FILENAME \
    && chmod -R 777 $JAVA_HOME

# Install server
ARG JAR_FILE=server/target/*.jar
COPY ${JAR_FILE} server.jar
ENTRYPOINT ["java","-jar","/server.jar"]
