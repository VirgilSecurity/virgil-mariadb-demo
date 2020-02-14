FROM adoptopenjdk:11-jdk-openj9

# Install tools
RUN apt-get update && apt-get install -y curl wget

# Install Maven
ENV MAVEN_HOME /opt/maven
ENV PATH ${PATH}:${MAVEN_HOME}/bin

ENV MAVEN_URL http://apache.volia.net/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
ENV MAVEN_FILENAME /tmp/maven.tar.gz

RUN mkdir -p $MAVEN_HOME && chmod -R 777 /opt
RUN wget $MAVEN_URL -O $MAVEN_FILENAME \
  && tar -xf $MAVEN_FILENAME --strip-components=1 -C $MAVEN_HOME \
  && rm $MAVEN_FILENAME \
  && chmod -R 777 $MAVEN_HOME

# Build an app
RUN mkdir -p server
COPY . server
WORKDIR server
RUN mvn clean package
RUN cp ./target/server.jar /server.jar

WORKDIR /
#COPY target/*.jar /server.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/server.jar"]
#ENTRYPOINT ["/bin/bash"]
