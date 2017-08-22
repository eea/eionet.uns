FROM tomcat:latest
RUN rm -rf /usr/local/tomcat/conf/logging.properties /usr/local/tomcat/webapps/*
COPY target/uns.war /usr/local/tomcat/webapps/ROOT.war