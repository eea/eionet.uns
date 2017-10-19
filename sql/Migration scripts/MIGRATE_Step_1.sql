-- Delete initial data
delete from UNS2_MIGRATION_TEST.CHANNEL;
delete from UNS2_MIGRATION_TEST.NOTIFICATION_TEMPLATE;
delete from UNS2_MIGRATION_TEST.EEA_USER;

-- Migrate Users
insert into UNS2_MIGRATION_TEST.EEA_USER
select ID,EXT_USER_ID,CN,VACATION_EXPIRATION,VACATION_FLAG, 60 as PAGE_REFRESH_DELAY,2 as NUMCOLUMNS,PREFER_HTML,0 as PREFER_DASHBOARD
from UNS1_PRODUCTION.eea_user;

-- Migrate Delivery Addresses
insert into UNS2_MIGRATION_TEST.DELIVERY_ADDRESS SELECT * FROM UNS1_PRODUCTION.delivery_address;

-- Migrate notification templates
insert into UNS2_MIGRATION_TEST.NOTIFICATION_TEMPLATE SELECT * FROM UNS1_PRODUCTION.notification_template;

-- Migrate Channels
insert into UNS2_MIGRATION_TEST.CHANNEL
SELECT
ID, 1 as STYLESHEET_ID, NOTIFICATION_TEMPLATE_ID, CREATOR, NAME AS TITLE, DESCRIPTION,
LAST_HARVEST, MODE, FEED_URL, REFRESH_DELAY, CREATION_DATE, CSTATUS, SECONDARY_ID, 'en'
FROM UNS1_PRODUCTION.channel;
-- Migrate Channel's delivery types
insert into UNS2_MIGRATION_TEST.CHANNEL_DELIVERY_TYPE
select * from UNS1_PRODUCTION.channel_delivery_type;

-- Migrate harvested events
insert into UNS2_MIGRATION_TEST.EVENT select
ID, CHANNEL_ID, EXT_ID, CREATION_DATE, RTYPE, PROCESSED
from UNS1_PRODUCTION.event;

-- Migrate harvested Event metadata
insert into UNS2_MIGRATION_TEST.EVENT_METADATA(EVENT_ID, PROPERTY, VALUE)
select EVENT_ID, PROPERTY, VALUE from UNS1_PRODUCTION.event_metadata;

-- Generate Channels  Metadata elements
insert into UNS2_MIGRATION_TEST.METADATA_ELEMENTS(NAME)
select distinct(a.PROPERTY) from UNS1_PRODUCTION.event_metadata a
UNION
select distinct(b.PROPERTY)from UNS1_PRODUCTION.choosable b;

-- Migrate subscriptions
insert into UNS2_MIGRATION_TEST.SUBSCRIPTION
select ID, EEA_USER_ID, CHANNEL_ID, LEAD_TIME, CREATION_DATE, SECONDARY_ID,
NULL as DASH_CORD_X, NULL as DASH_CORD_Y
FROM UNS1_PRODUCTION.subscription;
-- Migrate subscription Delivery types
insert into UNS2_MIGRATION_TEST.SUBSCRIPTION_DT
select * from UNS1_PRODUCTION.subscription_dt;
-- Migrate filters
insert into UNS2_MIGRATION_TEST.FILTER select * from UNS1_PRODUCTION.filter;
-- Migrate Filter's statements
insert into UNS2_MIGRATION_TEST.STATEMENT
SELECT s.FILTER_ID, M.NAME, s.VALUE, M.ID
FROM UNS1_PRODUCTION.statement s, UNS2_MIGRATION_TEST.METADATA_ELEMENTS M
where s.PROPERTY=M.NAME;

-- Migrate Generated Notifications
insert into UNS2_MIGRATION_TEST.NOTIFICATION
select ID, EEA_USER_ID, EVENT_ID, CONTENT, CHANNEL_ID, SUBJECT, HTML_CONTENT
from UNS1_PRODUCTION.notification;
-- Migrate Deliveries
insert into UNS2_MIGRATION_TEST.DELIVERY select * from UNS1_PRODUCTION.delivery;





