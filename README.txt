
*********************************************************************
This is the Reportnet's Unified Notification Service (UNS) software.
*********************************************************************

For doing a quick-start installation, read the following instructions. They
give some hints on how to quickly checkout UNS code from SVN, and how to set
it up as a Tomcat web application.

****************************************************************************

1. Create build directory for SVN checkout

shell>cd /var/local/build
shell>svn checkout https://svn.eionet.europa.eu/repositories/Reportnet/UNS/trunk uns

NB! The resulting /var/local/build/uns directory will be denoted below as $CHECKOUT_HOME

2. Create local.properties file by making a copy of default.properties.

shell>cd $CHECKOUT_HOME
shell>cp default.properties local.properties

3. In the freshly created local.properties file, change property values as
   appropriate for your environment. You will find meanings of every property
   from inside the file as comments.

4. Create UNS database and database user in MySql matching the values in local.properties.

mysql>create database uns;
mysql>CREATE USER 'unsuser'@'localhost' IDENTIFIED BY 'password-here';
mysql>GRANT ALL PRIVILEGES ON uns.* TO 'unsuser'@'localhost'

5. Import initial database structure and init data

shell>cd $CHECKOUT_HOME/sql
shell>mysql -u root -p uns < UNS2-create

6. Build the DD web application by issuing the following Maven command

shell>cd $CHECKOUT_HOME
shell>mvn -Dmaven.test.skip=true clean install

7. Place the resulting $CHECKOUT_HOME/target/uns.war into Tomcat's webapps directory, and start Tomcat.
