
rd /S /Q C:\dev\apache-tomcat-6.0.36\webapps\uns
rd /S /Q C:\dev\apache-tomcat-6.0.36\work\Catalina\localhost\uns
del /S /Q C:\dev\apache-tomcat-6.0.36\conf\Catalina\localhost\uns.xml
copy /Y .\target\uns.war C:\dev\apache-tomcat-6.0.36\webapps\uns.war
