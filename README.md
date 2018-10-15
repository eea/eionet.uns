## Unified Notification Service (UNS) software

### Prerequisites

* Java 1.8
* Maven 3.3.9
* Tomcat 8.5 or higher
* MySQL 5.5
* Docker 1.12 or higher

## Installation Guide

Create directory for GitHub checkout e.g /home/user/eea/uns

NB! The resulting /var/local/build/uns directory will be denoted below as $CHECKOUT_HOME

```shell
cd $CHECKOUT_HOME 
git clone https://github.com/eea/eionet.uns.git
```

Create local.properties file by making a copy of default.properties.

```shell
cp default.properties local.properties
```

In the freshly created local.properties file, change property values as
   appropriate for your environment. You will find meanings of every property
   from inside the file as comments.

Create UNS database and database user in MySql matching the values in local.properties.

```mysql
CREATE DATABASE uns;
CREATE USER 'unsuser'@'localhost' IDENTIFIED BY 'password-here';
GRANT ALL PRIVILEGES ON uns.* TO 'unsuser'@'localhost'
```

(Optional) Import initial database structure and init data. UNS is using a liquibase script to create the new database
so this step can be skipped.

```shell
cd $CHECKOUT_HOME/sql
mysql -u root -p uns < UNS2-create
```

Build the UNS web application by issuing the following maven command

```shell
cd $CHECKOUT_HOME
mvn -Denv=local -Dmaven.test.skip=true clean install
```

Place the resulting $CHECKOUT_HOME/target/uns.war into Tomcat's webapps directory, and start Tomcat.
