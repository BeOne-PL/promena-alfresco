FROM ${docker.acs.image}:${alfresco.platform.version}

ARG TOMCAT_DIR=/usr/local/tomcat

# Install alfresco-jvm-console
RUN yum install -y wget && \
    wget -O $TOMCAT_DIR/amps/alfresco-jvm-console-1.0.0.amp https://github.com/Skotar/alfresco-jvm-console/releases/download/1.0.0/alfresco-jvm-console-1.0.0.amp && \
    echo 'java -jar /usr/local/tomcat/alfresco-mmt/alfresco-mmt-6.0.jar install /usr/local/tomcat/amps/$1 /usr/local/tomcat/webapps/alfresco -force -nobackup' > install_amp.sh && chmod +x install_amp.sh && \
    ./install_amp.sh alfresco-jvm-console-1.0.0.amp && \
    rm install_amp.sh

# Copy Dockerfile to avoid an error if no JARs exist
COPY Dockerfile extensions/*.jar $TOMCAT_DIR/webapps/alfresco/WEB-INF/lib/

# Copy Dockerfile to avoid an error if no AMPs exist
COPY Dockerfile extensions/*.amp $TOMCAT_DIR/amps/
RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
              $TOMCAT_DIR/amps $TOMCAT_DIR/webapps/alfresco -directory -nobackup -force

COPY alfresco-global.properties $TOMCAT_DIR/shared/classes/alfresco-global.properties
COPY dev-log4j.properties $TOMCAT_DIR/shared/classes/alfresco/extension
COPY disable-webscript-caching-context.xml $TOMCAT_DIR/shared/classes/alfresco/extension

# Copy Dockerfile to avoid an error if no license file exists
COPY Dockerfile license/*.* $TOMCAT_DIR/webapps/alfresco/WEB-INF/classes/alfresco/extension/license/

# alfresco-jvm-console
RUN echo $' \n\
grant { \n\
    permission java.security.AllPermission; \n\
}; ' >> /usr/local/tomcat/conf/catalina.policy