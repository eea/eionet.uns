rd /S /Q C:\Tomcat\apache-tomcat-6.0.37\webapps\uns
rd /S /Q C:\Tomcat\apache-tomcat-6.0.37\work\Catalina\localhost\uns
del /S /Q C:\Tomcat\apache-tomcat-6.0.37\conf\Catalina\localhost\uns.xml
copy /Y .\target\uns.war C:\Tomcat\apache-tomcat-6.0.37\webapps\uns.war
