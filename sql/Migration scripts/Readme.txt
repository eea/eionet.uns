Migration scripts assume that you have two MySQL database schemas on the same DB server. 
One schema belongs to the UNS1 and another one to the UNS2.
In order to execute migration please execute the following steps (in specified order):

1.	Create new clean the UNS2 database schema by using sql script provided with the UNS2 source.
2.	Open all 3 migration scripts with a text editor and replace the following tokens:
      a.	"UNS2_MIGRATION_TEST" with name of your target UNS2 database schema.
      b.	"UNS1_PRODUCTION" with name of your UNS1 database schema.
3.	Execute script “MIGRATE_Step_1.sql”
4.	Execute script “MIGRATE_Step_2.sql” once for each channel form the UNS1. 
    You may make simple Java or Python script that will call this query for each channel if you would like to automate process.
5.	Execute “MIGRATE_Step_3.sql” script.

Migration should be completed successfully after execution of the above steps.
