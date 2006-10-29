/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE="NO_AUTO_VALUE_ON_ZERO" */;

--
-- Current Database: `UNS2`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `UNS2` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `UNS2`;

--
-- Table structure for table `ACLS`
--

DROP TABLE IF EXISTS `ACLS`;
CREATE TABLE `ACLS` (
  `ACL_ID` int(11) NOT NULL auto_increment,
  `ACL_NAME` varchar(100) NOT NULL default '',
  `PARENT_NAME` varchar(100) default NULL,
  `OWNER` varchar(255) NOT NULL default '',
  `DESCRIPTION` varchar(255) default '',
  PRIMARY KEY  (`ACL_ID`),
  UNIQUE KEY `ACL_NAME` (`ACL_NAME`,`PARENT_NAME`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `ACL_ROWS`
--

DROP TABLE IF EXISTS `ACL_ROWS`;
CREATE TABLE `ACL_ROWS` (
  `ACL_ID` int(11) NOT NULL default '0',
  `TYPE` enum('object','doc','dcc') NOT NULL default 'object',
  `ENTRY_TYPE` enum('owner','user','localgroup','other','foreign','unauthenticated','authenticated','mask') NOT NULL default 'user',
  `PRINCIPAL` char(16) NOT NULL default '',
  `PERMISSIONS` char(255) NOT NULL default '',
  `STATUS` int(1) default NULL,
  PRIMARY KEY  (`ACL_ID`,`TYPE`,`ENTRY_TYPE`,`PRINCIPAL`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Table structure for table `CHANNEL`
--

DROP TABLE IF EXISTS `CHANNEL`;
CREATE TABLE `CHANNEL` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `STYLESHEET_ID` int(10) unsigned default NULL,
  `NOTIFICATION_TEMPLATE_ID` int(10) unsigned default NULL,
  `CREATOR` int(10) unsigned NOT NULL default '0',
  `TITLE` varchar(255) character set utf8 NOT NULL default '',
  `DESCRIPTION` varchar(255) character set utf8 default NULL,
  `LAST_HARVEST` datetime default NULL,
  `MODE` varchar(4) character set utf8 NOT NULL default 'PULL',
  `FEED_URL` varchar(255) character set utf8 default NULL,
  `REFRESH_DELAY` int(10) unsigned default '600',
  `CREATION_DATE` datetime NOT NULL default '0000-00-00 00:00:00',
  `CSTATUS` tinyint(1) NOT NULL default '0',
  `SECONDARY_ID` varchar(255) character set utf8 default NULL,
  `LANGUAGE_ID` char(2) character set latin1 collate latin1_bin NOT NULL default '',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `IX_NAME_UNIQ` USING BTREE (`TITLE`),
  UNIQUE KEY `IX_URL_UINQUE` USING BTREE (`FEED_URL`),
  UNIQUE KEY `IX_SECID_UNIQ` USING BTREE (`SECONDARY_ID`),
  KEY `IX_EEAUSER_FK` (`CREATOR`),
  KEY `IX_TEMPLATE_FK` (`NOTIFICATION_TEMPLATE_ID`),
  KEY `CHANNEL_FKIndex3` (`STYLESHEET_ID`),
  CONSTRAINT `FK_NOTIFICATION_TEMPLATE` FOREIGN KEY (`NOTIFICATION_TEMPLATE_ID`) REFERENCES `NOTIFICATION_TEMPLATE` (`ID`),
  CONSTRAINT `CHANNEL_ibfk_1` FOREIGN KEY (`CREATOR`) REFERENCES `EEA_USER` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1 COMMENT='InnoDB free: 2571264 kB; (`ID`) REFER `UNS2/NOTIFICATION';

--
-- Table structure for table `CHANNEL_DELIVERY_TYPE`
--

DROP TABLE IF EXISTS `CHANNEL_DELIVERY_TYPE`;
CREATE TABLE `CHANNEL_DELIVERY_TYPE` (
  `CHANNEL_ID` int(10) unsigned NOT NULL default '0',
  `DELIVERY_TYPE_ID` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`CHANNEL_ID`,`DELIVERY_TYPE_ID`),
  KEY `IX_CHANNEL_FK` (`CHANNEL_ID`),
  KEY `IX_DT_FK` (`DELIVERY_TYPE_ID`),
  CONSTRAINT `CHANNEL_DELIVERY_TYPE_ibfk_1` FOREIGN KEY (`CHANNEL_ID`) REFERENCES `CHANNEL` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `CHANNEL_DELIVERY_TYPE_ibfk_2` FOREIGN KEY (`DELIVERY_TYPE_ID`) REFERENCES `DELIVERY_TYPE` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `CHANNEL_METADATA_ELEMENTS`
--

DROP TABLE IF EXISTS `CHANNEL_METADATA_ELEMENTS`;
CREATE TABLE `CHANNEL_METADATA_ELEMENTS` (
  `CHANNEL_ID` int(10) unsigned NOT NULL default '0',
  `METADATA_ELEMENT_ID` int(10) unsigned NOT NULL default '0',
  `VISIBLE` int(10) unsigned NOT NULL default '0',
  `APPEARANCE_ORDER` int(10) default '0',
  `FILTERED` tinyint(1) NOT NULL default '0',
  `OBSOLETE` tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (`CHANNEL_ID`,`METADATA_ELEMENT_ID`),
  KEY `IX_CHANNEL_FK` (`CHANNEL_ID`),
  KEY `IX_METADATA_ELEMENT_FK` (`METADATA_ELEMENT_ID`),
  CONSTRAINT `CHANNEL_METADATA_ELEMENTS_ibfk_1` FOREIGN KEY (`CHANNEL_ID`) REFERENCES `CHANNEL` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `CHANNEL_METADATA_ELEMENTS_ibfk_2` FOREIGN KEY (`METADATA_ELEMENT_ID`) REFERENCES `METADATA_ELEMENTS` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `CHANNEL_ROLE`
--

DROP TABLE IF EXISTS `CHANNEL_ROLE`;
CREATE TABLE `CHANNEL_ROLE` (
  `CHANNEL_ID` int(10) unsigned NOT NULL default '0',
  `EEA_ROLE_ID` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`CHANNEL_ID`,`EEA_ROLE_ID`),
  KEY `IX_CHANNEL_FK` (`CHANNEL_ID`),
  KEY `IX_ROLE_FK` (`EEA_ROLE_ID`),
  CONSTRAINT `CHANNEL_ROLE_ibfk_1` FOREIGN KEY (`CHANNEL_ID`) REFERENCES `CHANNEL` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `CHANNEL_ROLE_ibfk_2` FOREIGN KEY (`EEA_ROLE_ID`) REFERENCES `EEA_ROLE` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `DELIVERY`
--

DROP TABLE IF EXISTS `DELIVERY`;
CREATE TABLE `DELIVERY` (
  `NOTIFICATION_ID` int(10) unsigned NOT NULL default '0',
  `DELIVERY_TYPE_ID` int(10) unsigned NOT NULL default '0',
  `DELIVERY_STATUS` int(10) unsigned NOT NULL default '0',
  `DELIVERY_TIME` datetime NOT NULL default '0000-00-00 00:00:00',
  PRIMARY KEY  (`NOTIFICATION_ID`,`DELIVERY_TYPE_ID`),
  KEY `IX_NOTIFICATION_FK` (`NOTIFICATION_ID`),
  KEY `IX_DELIVERY_TYPE_FK` (`DELIVERY_TYPE_ID`),
  KEY `IX_DSTATUS` (`DELIVERY_STATUS`),
  CONSTRAINT `DELIVERY_ibfk_1` FOREIGN KEY (`NOTIFICATION_ID`) REFERENCES `NOTIFICATION` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `DELIVERY_ibfk_2` FOREIGN KEY (`DELIVERY_TYPE_ID`) REFERENCES `DELIVERY_TYPE` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `DELIVERY_ADDRESS`
--

DROP TABLE IF EXISTS `DELIVERY_ADDRESS`;
CREATE TABLE `DELIVERY_ADDRESS` (
  `EEA_USER_ID` int(10) unsigned NOT NULL default '0',
  `DELIVERY_TYPE_ID` int(10) unsigned NOT NULL default '0',
  `ADDRESS` varchar(255) character set utf8 default NULL,
  PRIMARY KEY  (`EEA_USER_ID`,`DELIVERY_TYPE_ID`),
  KEY `IX_EEA_USER_FK` (`EEA_USER_ID`),
  KEY `IX_DELIVERY_TYPE_FK` (`DELIVERY_TYPE_ID`),
  CONSTRAINT `DELIVERY_ADDRESS_ibfk_1` FOREIGN KEY (`EEA_USER_ID`) REFERENCES `EEA_USER` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `DELIVERY_ADDRESS_ibfk_2` FOREIGN KEY (`DELIVERY_TYPE_ID`) REFERENCES `DELIVERY_TYPE` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `DELIVERY_TYPE`
--

DROP TABLE IF EXISTS `DELIVERY_TYPE`;
CREATE TABLE `DELIVERY_TYPE` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `NAME` varchar(45) character set utf8 NOT NULL default '',
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `EEA_ROLE`
--

DROP TABLE IF EXISTS `EEA_ROLE`;
CREATE TABLE `EEA_ROLE` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `EXT_ID` varchar(255) character set utf8 NOT NULL default '',
  PRIMARY KEY  (`ID`),
  KEY `Index_2` (`EXT_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `EEA_USER`
--

DROP TABLE IF EXISTS `EEA_USER`;
CREATE TABLE `EEA_USER` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `EXT_USER_ID` varchar(255) character set utf8 NOT NULL default '',
  `CN` varchar(255) character set utf8 NOT NULL default '',
  `VACATION_EXPIRATION` datetime default NULL,
  `VACATION_FLAG` tinyint(1) NOT NULL default '0',
  `PAGE_REFRESH_DELAY` int(10) unsigned default NULL,
  `NUMCOLUMNS` smallint(5) unsigned default NULL,
  `PREFER_HTML` tinyint(1) NOT NULL default '0',
  `PREFER_DASHBOARD` tinyint(1) NOT NULL default '1',
  PRIMARY KEY  (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `EVENT`
--

DROP TABLE IF EXISTS `EVENT`;
CREATE TABLE `EVENT` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `CHANNEL_ID` int(10) unsigned NOT NULL default '0',
  `EXT_ID` varchar(255) character set utf8 NOT NULL default '',
  `CREATION_DATE` datetime NOT NULL default '0000-00-00 00:00:00',
  `RTYPE` varchar(255) character set utf8 NOT NULL default '',
  `PROCESSED` tinyint(3) unsigned NOT NULL default '0',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `IX_EXT_ID_UNIQ` USING BTREE (`EXT_ID`),
  KEY `IX_CHANNEL_FK` (`CHANNEL_ID`),
  KEY `IX_CREATION_DATE` (`CREATION_DATE`),
  CONSTRAINT `EVENT_ibfk_1` FOREIGN KEY (`CHANNEL_ID`) REFERENCES `CHANNEL` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `EVENT_METADATA`
--

DROP TABLE IF EXISTS `EVENT_METADATA`;
CREATE TABLE `EVENT_METADATA` (
  `EVENT_ID` int(10) unsigned NOT NULL default '0',
  `PROPERTY` varchar(255) character set utf8 NOT NULL default '',
  `VALUE` text character set utf8,
  `ID` int(10) unsigned NOT NULL auto_increment,
  PRIMARY KEY  (`ID`),
  KEY `EVENT_METADATA_FKIndex1` (`EVENT_ID`),
  CONSTRAINT `EVENT_METADATA_ibfk_1` FOREIGN KEY (`EVENT_ID`) REFERENCES `EVENT` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `FILTER`
--

DROP TABLE IF EXISTS `FILTER`;
CREATE TABLE `FILTER` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `SUBSCRIPTION_ID` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`ID`),
  KEY `IX_SUBSCRIPTION_FK` (`SUBSCRIPTION_ID`),
  CONSTRAINT `FILTER_ibfk_1` FOREIGN KEY (`SUBSCRIPTION_ID`) REFERENCES `SUBSCRIPTION` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `METADATA_ELEMENTS`
--

DROP TABLE IF EXISTS `METADATA_ELEMENTS`;
CREATE TABLE `METADATA_ELEMENTS` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `NAME` varchar(255) character set utf8 NOT NULL default '',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `IX_METADATANAME_UNIQ` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `NOTIFICATION`
--

DROP TABLE IF EXISTS `NOTIFICATION`;
CREATE TABLE `NOTIFICATION` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `EEA_USER_ID` int(10) unsigned NOT NULL default '0',
  `EVENT_ID` int(10) unsigned NOT NULL default '0',
  `CONTENT` text character set utf8 NOT NULL,
  `CHANNEL_ID` int(10) unsigned NOT NULL default '0',
  `SUBJECT` varchar(255) character set utf8 default NULL,
  `HTML_CONTENT` text,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `IX_USEREV_UNIQ` USING BTREE (`EEA_USER_ID`,`EVENT_ID`),
  KEY `IX_EVENT_FK` (`EVENT_ID`),
  KEY `IX_EEA_USER_FK` (`EEA_USER_ID`),
  CONSTRAINT `NOTIFICATION_ibfk_1` FOREIGN KEY (`EVENT_ID`) REFERENCES `EVENT` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `NOTIFICATION_ibfk_2` FOREIGN KEY (`EEA_USER_ID`) REFERENCES `EEA_USER` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `NOTIFICATION_TEMPLATE`
--

DROP TABLE IF EXISTS `NOTIFICATION_TEMPLATE`;
CREATE TABLE `NOTIFICATION_TEMPLATE` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `NAME` varchar(255) character set utf8 NOT NULL default '',
  `SUBJECT` varchar(255) character set utf8 default NULL,
  `TEXT_PLAIN` text character set utf8,
  `TEXT_HTML` text character set utf8,
  `EDIT_ONLY` tinyint(1) default '0',
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `IX_TNAME_UNIQ` USING BTREE (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `STATEMENT`
--

DROP TABLE IF EXISTS `STATEMENT`;
CREATE TABLE `STATEMENT` (
  `FILTER_ID` int(10) unsigned default NULL,
  `PROPERTY` varchar(255) character set utf8 NOT NULL default '',
  `VALUE` varchar(255) character set utf8 NOT NULL default '',
  `METADATA_ELEMENT_ID` int(10) unsigned NOT NULL default '0',
  KEY `IX_PROPERTY` (`PROPERTY`),
  KEY `IX_VALUE` (`VALUE`),
  KEY `IX_FILTER_FK` (`FILTER_ID`),
  KEY `FK_STATEMENT_2` (`METADATA_ELEMENT_ID`),
  CONSTRAINT `FK_STATEMENT_2` FOREIGN KEY (`METADATA_ELEMENT_ID`) REFERENCES `METADATA_ELEMENTS` (`ID`),
  CONSTRAINT `STATEMENT_ibfk_1` FOREIGN KEY (`FILTER_ID`) REFERENCES `FILTER` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `STYLESHEET`
--

DROP TABLE IF EXISTS `STYLESHEET`;
CREATE TABLE `STYLESHEET` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `NAME` varchar(50) character set utf8 NOT NULL default '',
  `DESCRIPTION` varchar(255) character set utf8 NOT NULL default '',
  `CONTENT` text character set utf8 NOT NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `IX_NAME_UNIQ` (`NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `SUBSCRIPTION`
--

DROP TABLE IF EXISTS `SUBSCRIPTION`;
CREATE TABLE `SUBSCRIPTION` (
  `ID` int(10) unsigned NOT NULL auto_increment,
  `EEA_USER_ID` int(10) unsigned NOT NULL default '0',
  `CHANNEL_ID` int(10) unsigned NOT NULL default '0',
  `LEAD_TIME` int(10) unsigned default NULL,
  `CREATION_DATE` datetime NOT NULL default '0000-00-00 00:00:00',
  `SECONDARY_ID` varchar(255) character set utf8 NOT NULL default '',
  `DASH_CORD_X` smallint(5) default NULL,
  `DASH_CORD_Y` smallint(5) default NULL,
  PRIMARY KEY  (`ID`),
  UNIQUE KEY `IX_USER_CHANNEL_UNIQ` (`EEA_USER_ID`,`CHANNEL_ID`),
  UNIQUE KEY `IX_SECID_UNIQ` USING BTREE (`SECONDARY_ID`),
  KEY `IX_CHANNEL_FK` (`CHANNEL_ID`),
  KEY `IX_EEA_USER_FK` (`EEA_USER_ID`),
  KEY `IX_SCREATION_DATE` (`CREATION_DATE`),
  CONSTRAINT `SUBSCRIPTION_ibfk_1` FOREIGN KEY (`CHANNEL_ID`) REFERENCES `CHANNEL` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `SUBSCRIPTION_ibfk_2` FOREIGN KEY (`EEA_USER_ID`) REFERENCES `EEA_USER` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Table structure for table `SUBSCRIPTION_DT`
--

DROP TABLE IF EXISTS `SUBSCRIPTION_DT`;
CREATE TABLE `SUBSCRIPTION_DT` (
  `SUBSCRIPTION_ID` int(10) unsigned NOT NULL default '0',
  `DELIVERY_TYPE_ID` int(10) unsigned NOT NULL default '0',
  PRIMARY KEY  (`SUBSCRIPTION_ID`,`DELIVERY_TYPE_ID`),
  KEY `IX_SUBSCRIPTION` (`SUBSCRIPTION_ID`),
  KEY `IX_DELIVERY_TYPE_FK` (`DELIVERY_TYPE_ID`),
  CONSTRAINT `SUBSCRIPTION_DT_ibfk_1` FOREIGN KEY (`SUBSCRIPTION_ID`) REFERENCES `SUBSCRIPTION` (`ID`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `SUBSCRIPTION_DT_ibfk_2` FOREIGN KEY (`DELIVERY_TYPE_ID`) REFERENCES `DELIVERY_TYPE` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;




--
-- Insert system user
--
/*!40000 ALTER TABLE `EEA_USER` DISABLE KEYS */;
INSERT INTO `EEA_USER` (`ID`,`EXT_USER_ID`,`CN`,`VACATION_EXPIRATION`,`VACATION_FLAG`,`PAGE_REFRESH_DELAY`,`NUMCOLUMNS`,`PREFER_HTML`,`PREFER_DASHBOARD`) VALUES
 (1,'system','system',NULL,0,0,0,0,0);
/*!40000 ALTER TABLE `EEA_USER` ENABLE KEYS */;



--
-- Insert all delivery types
--

/*!40000 ALTER TABLE `DELIVERY_TYPE` DISABLE KEYS */;
INSERT INTO `DELIVERY_TYPE` (`ID`,`NAME`) VALUES
 (1,'EMAIL'),
 (2,'JABBER'),
 (3,'WDB'),
 (4,'RSS');
/*!40000 ALTER TABLE `DELIVERY_TYPE` ENABLE KEYS */;



--
-- Insert basic notification templates
--
/*!40000 ALTER TABLE `NOTIFICATION_TEMPLATE` DISABLE KEYS */;
INSERT INTO `NOTIFICATION_TEMPLATE` (`ID`,`NAME`,`SUBJECT`,`TEXT_PLAIN`,`TEXT_HTML`,`EDIT_ONLY`) VALUES
 (1,'Default template','\"$EVENT.CHANNEL\" channel: $EVENT.TITLE','$EVENT\r\nYou may unsubscribe yourself from the \"$EVENT.CHANNEL\" channel by using the following link: $UNSUSCRIBE_LINK\r\ntest 001\r\nBest Regards,\r\nEuropean Environment Agency','<p>$EVENT<br />You may unsubscribe yourself from the \"$EVENT.CHANNEL\" channel by using the following link: $UNSUSCRIBE_LINK</p><p><br /></p><p>Best Regards,<br />European Environment Agency</p><p></p>',1),
 (2,'Unsusbcribed - channel access rights changed notification','Unsubscribed from \"$EVENT.CHANNEL\" channel','You have been unsubscribed form the channel \"$EVENT.CHANNEL\" due to access rigths changes.\r\n \r\nSorry for the inconvenience,\r\nEuropean Environment Agency','You have been unsubscribed form the channel &quot;$EVENT.CHANNEL&quot; due to access rigths changes.<br /> &nbsp;<br /> Sorry for the inconvenience,<br /> European Environment Agency<br />',1),
 (3,'Unsubscribed - channel removed notification','Unsubscribed from \"$EVENT.CHANNEL\" channel','Channel \"$EVENT.CHANNEL\" does not exist anymore.\r\nYou will not be receving notification for this channel anymore.\r\n \r\nSorry for the inconvenience,\r\nEuropean Environment Agency','Channel \"$EVENT.CHANNEL\" does not exist anymore.<br /> You will not be receving notification for this channel anymore.<br />  <br /> Sorry for the inconvenience,<br /> European Environment Agency',1);
/*!40000 ALTER TABLE `NOTIFICATION_TEMPLATE` ENABLE KEYS */;



--
-- Insert basic stylesheets
--
/*!40000 ALTER TABLE `STYLESHEET` DISABLE KEYS */;
INSERT INTO `STYLESHEET` (`ID`,`NAME`,`DESCRIPTION`,`CONTENT`) VALUES
 (1,'default.xsl','This stylesheet is able to transform any RSS(0.9x, 1.0 and 2.0) feed to simple HTML','<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\r\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\r\n    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n    xmlns:ev=\"http://purl.org/rss/1.0/modules/event/\" xmlns:rss=\"http://purl.org/rss/1.0/\"\r\n    xmlns:dc=\"http://purl.org/dc/elements/1.1/\" exclude-result-prefixes=\"ev rdf rss dc\"\r\n    version=\"1.0\">\r\n    <xsl:output indent=\"yes\" method=\"html\" omit-xml-declaration=\"yes\"/>\r\n    <xsl:param name=\"openinpopup\" select=\"\'true\'\"/>\r\n    <xsl:param name=\"showdescription\" select=\"\'true\'\"/>\r\n    <xsl:param name=\"showtitle\" select=\"\'true\'\"/>\r\n    <xsl:template match=\"/rss\">\r\n        <xsl:apply-templates select=\"channel\"/>\r\n    </xsl:template>\r\n    <xsl:template match=\"/rdf:RDF\">\r\n        <ul>\r\n            <xsl:apply-templates select=\"rss:item\"/>\r\n        </ul>\r\n    </xsl:template>\r\n    <xsl:template match=\"channel\">\r\n        <ul>\r\n            <xsl:apply-templates select=\"item\"/>\r\n        </ul>\r\n    </xsl:template>\r\n    <xsl:template match=\"rss:item\">\r\n        <xsl:variable name=\"description\" select=\"rss:description|dc:description\"/>\r\n        <li>\r\n            <a>\r\n                <xsl:attribute name=\"href\">\r\n                    <xsl:value-of select=\"rss:link|dc:identifier\"/>\r\n                </xsl:attribute>\r\n                <xsl:if test=\"$openinpopup = \'true\'\">\r\n                    <xsl:attribute name=\"target\">_blank</xsl:attribute>\r\n                </xsl:if>\r\n                <xsl:value-of select=\"rss:title|dc:title\"/>\r\n            </a>\r\n            <xsl:if test=\"$showdescription = \'true\' and $description\">\r\n                <p>\r\n                    <xsl:value-of select=\"$description\"/>\r\n                </p>\r\n            </xsl:if>\r\n        </li>\r\n    </xsl:template>\r\n    <xsl:template match=\"item\">\r\n        <xsl:variable name=\"description\" select=\"description\"/>\r\n        <li>\r\n            <a>\r\n                <xsl:attribute name=\"href\">\r\n                    <xsl:value-of select=\"link\"/>\r\n                </xsl:attribute>\r\n                <xsl:if test=\"$openinpopup = \'true\'\">\r\n                    <xsl:attribute name=\"target\">_blank</xsl:attribute>\r\n                </xsl:if>\r\n                <xsl:value-of select=\"title\"/>\r\n            </a>\r\n            <xsl:if test=\"$showdescription = \'true\' and $description\">\r\n                <p>\r\n                    <xsl:value-of select=\"$description\"/>\r\n                </p>\r\n            </xsl:if>\r\n        </li>\r\n    </xsl:template>\r\n</xsl:stylesheet>\r\n'),
 (2,'tograph.xsl','Transforms \"All obligations\" ROD feed to the SVG representing statistic of obligations per year','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<xsl:stylesheet exclude-result-prefixes=\"ev rdf rss\" version=\"1.0\"\r\n    xmlns:ev=\"http://purl.org/rss/1.0/modules/event/\"\r\n    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n    xmlns:rss=\"http://purl.org/rss/1.0/\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n    <xsl:output indent=\"yes\" method=\"html\" omit-xml-declaration=\"yes\"/>\r\n    <xsl:variable name=\"totalObligations\">\r\n        <xsl:value-of select=\"count(//*[name()=\'item\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:variable name=\"year2001\">\r\n        <xsl:value-of select=\"count(//*[substring(ev:startdate,1,4)=\'2001\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:variable name=\"year2002\">\r\n        <xsl:value-of select=\"count(//*[substring(ev:startdate,1,4)=\'2002\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:variable name=\"year2003\">\r\n        <xsl:value-of select=\"count(//*[substring(ev:startdate,1,4)=\'2003\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:variable name=\"year2004\">\r\n        <xsl:value-of select=\"count(//*[substring(ev:startdate,1,4)=\'2004\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:variable name=\"year2005\">\r\n        <xsl:value-of select=\"count(//*[substring(ev:startdate,1,4)=\'2005\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:variable name=\"year2006\">\r\n        <xsl:value-of select=\"count(//*[substring(ev:startdate,1,4)=\'2006\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:variable name=\"year2007\">\r\n        <xsl:value-of select=\"count(//*[substring(ev:startdate,1,4)=\'2007\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:variable name=\"year2008\">\r\n        <xsl:value-of select=\"count(//*[substring(ev:startdate,1,4)=\'2008\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:variable name=\"year2009\">\r\n        <xsl:value-of select=\"count(//*[substring(ev:startdate,1,4)=\'2009\'])\"/>\r\n    </xsl:variable>\r\n    <xsl:template match=\"/\">\r\n        <svg height=\"160\" version=\"1.1\" width=\"550\"\r\n            xmlns=\"http://www.w3.org/2000/svg\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">\r\n            <path d=\"M0 0 L0 420 L800 420 L800 0 Z\" style=\"fill:#F0F0F0;stroke:none;\"/>\r\n            <text fill=\"black\" font-size=\"12\" x=\"180\" y=\"0\">\r\n                <tspan dy=\"10\" x=\"120\">OBLIGATIONS NUMBER PER YEAR 2001-2009</tspan>\r\n            </text>\r\n            <!-- Draw the x and y axes  -->\r\n            <line stroke=\"black\" x1=\"30\" x2=\"500\" y1=\"150\" y2=\"150\"/>\r\n            <line stroke=\"black\" x1=\"30\" x2=\"30\" y1=\"150\" y2=\"0\"/>\r\n            <path d=\"M26 10 L30 0 L34 10 Z\"/>\r\n            <path d=\"M495 145 L500 150 L495 155 Z\"/>\r\n            <!-- y-axis labels -->\r\n            <g style=\"font-size:10\">\r\n                <text x=\"3\" y=\"130\">30</text>\r\n                <text x=\"3\" y=\"110\">60</text>\r\n                <text x=\"3\" y=\"90\">90</text>\r\n                <text x=\"3\" y=\"70\">120</text>\r\n                <text x=\"3\" y=\"50\">150</text>\r\n                <text x=\"3\" y=\"30\">180</text>\r\n            </g>\r\n            <!-- x-axis labels -->\r\n            <g style=\"font-size:10\">\r\n                <text x=\"50\" y=\"160\">2001</text>\r\n                <text x=\"90\" y=\"160\">2002</text>\r\n                <text x=\"130\" y=\"160\">2003</text>\r\n                <text x=\"170\" y=\"160\">2004</text>\r\n                <text x=\"210\" y=\"160\">2005</text>\r\n                <text x=\"250\" y=\"160\">2006</text>\r\n                <text x=\"290\" y=\"160\">2007</text>\r\n                <text x=\"330\" y=\"160\">2008</text>\r\n                <text x=\"370\" y=\"160\">2009</text>\r\n            </g>\r\n            <!-- Draw data -->\r\n            <circle cx=\"50\" cy=\"{150-$year2001*0.65}\" fill=\"red\" r=\"4\"\r\n                stroke=\"black\" stroke-width=\"2\"/>\r\n            <circle cx=\"90\" cy=\"{150-$year2002*0.65}\" fill=\"red\" r=\"4\"\r\n                stroke=\"black\" stroke-width=\"2\"/>\r\n            <circle cx=\"130\" cy=\"{150-$year2003*0.65}\" fill=\"red\" r=\"4\"\r\n                stroke=\"black\" stroke-width=\"2\"/>\r\n            <circle cx=\"170\" cy=\"{150-$year2004*0.65}\" fill=\"red\" r=\"4\"\r\n                stroke=\"black\" stroke-width=\"2\"/>\r\n            <circle cx=\"210\" cy=\"{150-$year2005*0.65}\" fill=\"red\" r=\"4\"\r\n                stroke=\"black\" stroke-width=\"2\"/>\r\n            <circle cx=\"250\" cy=\"{150-$year2006*0.65}\" fill=\"red\" r=\"4\"\r\n                stroke=\"black\" stroke-width=\"2\"/>\r\n            <circle cx=\"290\" cy=\"{150-$year2007*0.65}\" fill=\"red\" r=\"4\"\r\n                stroke=\"black\" stroke-width=\"2\"/>\r\n            <circle cx=\"330\" cy=\"{150-$year2008*0.65}\" fill=\"red\" r=\"4\"\r\n                stroke=\"black\" stroke-width=\"2\"/>\r\n            <circle cx=\"370\" cy=\"{150-$year2009*0.65}\" fill=\"red\" r=\"4\"\r\n                stroke=\"black\" stroke-width=\"2\"/>\r\n            <line stroke=\"black\" x1=\"50\" x2=\"90\"\r\n                y1=\"{150-$year2001*0.65}\" y2=\"{150-$year2002*0.65}\"/>\r\n            <line stroke=\"black\" x1=\"90\" x2=\"130\"\r\n                y1=\"{150-$year2002*0.65}\" y2=\"{150-$year2003*0.65}\"/>\r\n            <line stroke=\"black\" x1=\"130\" x2=\"170\"\r\n                y1=\"{150-$year2003*0.65}\" y2=\"{150-$year2004*0.65}\"/>\r\n            <line stroke=\"black\" x1=\"170\" x2=\"210\"\r\n                y1=\"{150-$year2004*0.65}\" y2=\"{150-$year2005*0.65}\"/>\r\n            <line stroke=\"black\" x1=\"210\" x2=\"250\"\r\n                y1=\"{150-$year2005*0.65}\" y2=\"{150-$year2006*0.65}\"/>\r\n            <line stroke=\"black\" x1=\"250\" x2=\"290\"\r\n                y1=\"{150-$year2006*0.65}\" y2=\"{150-$year2007*0.65}\"/>\r\n            <line stroke=\"black\" x1=\"290\" x2=\"330\"\r\n                y1=\"{150-$year2007*0.65}\" y2=\"{150-$year2008*0.65}\"/>\r\n            <line stroke=\"black\" x1=\"330\" x2=\"370\"\r\n                y1=\"{150-$year2008*0.65}\" y2=\"{150-$year2009*0.65}\"/>\r\n        </svg>\r\n    </xsl:template>\r\n</xsl:stylesheet>\r\n');
 INSERT INTO `STYLESHEET` (`ID`,`NAME`,`DESCRIPTION`,`CONTENT`) VALUES 
 (3,'push.xsl','Stylsheet used by push channels','<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\r\n<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"\r\n    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n    xmlns:ev=\"http://purl.org/rss/1.0/modules/event/\" xmlns:rss=\"http://purl.org/rss/1.0/\"\r\n    xmlns:dc=\"http://purl.org/dc/elements/1.1/\" exclude-result-prefixes=\"ev rdf rss dc\"\r\n    xmlns:wdb=\"http://wdb.eionet.eu.int/elements#\"\r\n    version=\"1.0\">\r\n    <xsl:output indent=\"yes\" method=\"html\" omit-xml-declaration=\"yes\"/>\r\n    <xsl:param name=\"openinpopup\" select=\"\'false\'\"/>\r\n    <xsl:param name=\"showdescription\" select=\"\'true\'\"/>\r\n    <xsl:param name=\"showtitle\" select=\"\'true\'\"/>\r\n    <xsl:template match=\"/rss\">\r\n        <xsl:apply-templates select=\"channel\">\r\n            <xsl:sort select=\"wdb:created\" order=\"descending\"/>\r\n        </xsl:apply-templates>\r\n    </xsl:template>\r\n    <xsl:template match=\"/rdf:RDF\">\r\n        <ul>\r\n            <xsl:apply-templates select=\"rss:item\">\r\n               <xsl:sort select=\"wdb:created\" order=\"descending\"/>\r\n            </xsl:apply-templates>\r\n        </ul>\r\n    </xsl:template>\r\n    <xsl:template match=\"channel\">\r\n        <ul>\r\n            <xsl:apply-templates select=\"item\"/>\r\n        </ul>\r\n    </xsl:template>\r\n    <xsl:template match=\"rss:item\">\r\n        <xsl:variable name=\"description\" select=\"rss:description|dc:description\"/>\r\n        <li>\r\n            <a>\r\n                <xsl:attribute name=\"href\">\r\n                    <xsl:value-of select=\"rss:link|dc:identifier\"/>\r\n                </xsl:attribute>\r\n                <xsl:if test=\"$openinpopup = \'true\'\">\r\n                    <xsl:attribute name=\"target\">_blank</xsl:attribute>\r\n                </xsl:if>\r\n                <xsl:value-of select=\"rss:title|dc:title\"/>\r\n            </a>\r\n            <xsl:if test=\"$showdescription = \'true\' and $description\">\r\n                <p>\r\n                    <xsl:value-of select=\"$description\"/>\r\n                </p>\r\n            </xsl:if>\r\n        </li>\r\n    </xsl:template>\r\n    <xsl:template match=\"item\">\r\n        <xsl:variable name=\"description\" select=\"description\"/>\r\n        <li>\r\n            <a>\r\n                <xsl:attribute name=\"href\">\r\n                    <xsl:value-of select=\"link\"/>\r\n                </xsl:attribute>\r\n                <xsl:if test=\"$openinpopup = \'true\'\">\r\n                    <xsl:attribute name=\"target\">_blank</xsl:attribute>\r\n                </xsl:if>\r\n                <xsl:value-of select=\"title\"/>\r\n            </a>\r\n            <xsl:if test=\"$showdescription = \'true\' and $description\">\r\n                <p>\r\n                    <xsl:value-of select=\"$description\"/>\r\n                </p>\r\n            </xsl:if>\r\n        </li>\r\n    </xsl:template>\r\n</xsl:stylesheet>\r\n');
INSERT INTO `STYLESHEET` (`ID`,`NAME`,`DESCRIPTION`,`CONTENT`) VALUES 
 (4,'peifer_obligations.xsl','Obligation dealines with Peifer example dashboard images','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<xsl:stylesheet exclude-result-prefixes=\"ev rdf rss dc\" version=\"1.0\"\r\n    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n    xmlns:ev=\"http://purl.org/rss/1.0/modules/event/\"\r\n    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n    xmlns:rss=\"http://purl.org/rss/1.0/\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n    <xsl:output indent=\"yes\" method=\"xhtml\" omit-xml-declaration=\"yes\"/>\r\n    <xsl:variable name=\"currentMonth\" select=\"month-from-date(current-date())\"/>\r\n    <xsl:variable name=\"currentYear\" select=\"year-from-date(current-date())\"/>\r\n    <xsl:template match=\"/rdf:RDF\">\r\n        <table style=\"table-layout: fixed;\">\r\n            <xsl:apply-templates>\r\n                <xsl:sort select=\"ev:startdate\"/>\r\n            </xsl:apply-templates>\r\n        </table>\r\n    </xsl:template>\r\n    <xsl:template match=\"rss:item\">\r\n        <xsl:variable name=\"description\" select=\"rss:description|dc:description\"/>\r\n        <xsl:variable name=\"oblYear\" select=\"substring(ev:startdate,1,4)\"/>\r\n        <xsl:variable name=\"oblMonth\" select=\"substring(substring(ev:startdate,6,7),1,2)\"/>\r\n        <xsl:if test=\"$oblYear = $currentYear\">\r\n            <tr>\r\n                <td style=\"width: 25px;\" valign=\"top\">\r\n                    <xsl:element name=\"img\">\r\n                        <xsl:attribute name=\"alt\">clck</xsl:attribute>\r\n                        <xsl:attribute name=\"title\">clock</xsl:attribute>\r\n                        <xsl:choose>\r\n                            <xsl:when test=\"$oblMonth &lt; $currentMonth\">\r\n                                <xsl:attribute name=\"src\">../../images/gallery/peifer/pclock.png</xsl:attribute>\r\n                            </xsl:when>\r\n                            <xsl:when test=\"$currentMonth=$oblMonth\">\r\n                                <xsl:attribute name=\"src\">../../images/gallery/peifer/nclock.png</xsl:attribute>\r\n                            </xsl:when>\r\n                            <xsl:otherwise>\r\n                                <xsl:attribute name=\"src\">../../images/gallery/peifer/fclock.png</xsl:attribute>\r\n                            </xsl:otherwise>\r\n                        </xsl:choose>\r\n                    </xsl:element>\r\n                </td>\r\n                <td nowrap=\"nowrap\" style=\"width: 70px;\" valign=\"top\">\r\n                    <xsl:value-of select=\"ev:startdate\"/>\r\n                </td>\r\n                <td valign=\"top\">\r\n                    <a>\r\n                        <xsl:attribute name=\"href\">\r\n                            <xsl:value-of select=\"rss:link|dc:identifier\"/>\r\n                        </xsl:attribute>\r\n                        <xsl:attribute name=\"target\">_blank</xsl:attribute>\r\n                        <xsl:value-of select=\"rss:title|dc:title\"/>\r\n                    </a>\r\n                </td>\r\n            </tr>\r\n        </xsl:if>\r\n    </xsl:template>\r\n</xsl:stylesheet>\r\n');
INSERT INTO `STYLESHEET` (`ID`,`NAME`,`DESCRIPTION`,`CONTENT`) VALUES 
 (5,'peifer_roles.xsl','Directory roles with Peifer example dashboard images','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<xsl:stylesheet exclude-result-prefixes=\"ev rdf rss dc\" version=\"1.0\"\r\n    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n    xmlns:ev=\"http://purl.org/rss/1.0/modules/event/\"\r\n    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n    xmlns:rss=\"http://purl.org/rss/1.0/\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n    <xsl:output indent=\"yes\" method=\"xhtml\" omit-xml-declaration=\"yes\"/>\r\n    <xsl:param name=\"openinpopup\" select=\"\'false\'\"/>\r\n    <xsl:param name=\"showdescription\" select=\"\'true\'\"/>\r\n    <xsl:param name=\"showtitle\" select=\"\'true\'\"/>\r\n    <xsl:template match=\"/rss\">\r\n        <xsl:apply-templates select=\"channel\"/>\r\n    </xsl:template>\r\n    <xsl:template match=\"channel\">\r\n        <table>\r\n            <xsl:apply-templates select=\"item\"/>\r\n        </table>\r\n    </xsl:template>\r\n    <xsl:template match=\"item\">\r\n        <xsl:variable name=\"description\" select=\"description\"/>\r\n        <tr>\r\n            <td rowspan=\"3\" valign=\"top\">\r\n                <img alt=\"o\" src=\"../../images/gallery/peifer/b_role-userdetails-on.gif\"/>\r\n            </td>\r\n            <td/>\r\n        </tr>\r\n        <tr>\r\n            <td valign=\"top\">\r\n                <a>\r\n                    <xsl:attribute name=\"href\">\r\n                        <xsl:value-of select=\"link\"/>\r\n                    </xsl:attribute>\r\n                    <xsl:attribute name=\"target\">_blank</xsl:attribute>\r\n                    <xsl:value-of select=\"title\"/>\r\n                </a>\r\n            </td>\r\n        </tr>\r\n        <tr>\r\n            <td>\r\n                <xsl:if test=\"$description\">\r\n                    <xsl:value-of select=\"$description\"/>\r\n                </xsl:if>\r\n            </td>\r\n        </tr>\r\n    </xsl:template>\r\n</xsl:stylesheet>\r\n'),
 (6,'peifer_users.xsl','Directory users with Peifer example dashbaord images','<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<xsl:stylesheet exclude-result-prefixes=\"ev rdf rss dc\" version=\"1.0\"\r\n    xmlns:dc=\"http://purl.org/dc/elements/1.1/\"\r\n    xmlns:ev=\"http://purl.org/rss/1.0/modules/event/\"\r\n    xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\r\n    xmlns:rss=\"http://purl.org/rss/1.0/\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n    <xsl:output indent=\"yes\" method=\"xhtml\" omit-xml-declaration=\"yes\"/>\r\n    <xsl:template match=\"/rss\">\r\n        <xsl:apply-templates select=\"channel\"/>\r\n    </xsl:template>\r\n    <xsl:template match=\"channel\">\r\n        <table>\r\n            <xsl:apply-templates select=\"item\"/>\r\n        </table>\r\n    </xsl:template>\r\n    <xsl:template match=\"item\">\r\n        <xsl:variable name=\"description\" select=\"description\"/>\r\n        <tr>\r\n            <td rowspan=\"3\" valign=\"top\">\r\n                <xsl:choose>\r\n                    <xsl:when test=\"contains(title,\'addition\')\">\r\n                        <img alt=\"o\" src=\"../../images/gallery/peifer/add-member.gif\"/>\r\n                    </xsl:when>\r\n                    <xsl:otherwise>\r\n                        <img alt=\"o\" src=\"../../images/gallery/peifer/del-member.gif\"/>\r\n                    </xsl:otherwise>\r\n                </xsl:choose>\r\n            </td>\r\n            <td/>\r\n        </tr>\r\n        <tr>\r\n            <td valign=\"top\">\r\n                <a>\r\n                    <xsl:attribute name=\"href\">\r\n                        <xsl:value-of select=\"link\"/>\r\n                    </xsl:attribute>\r\n                    <xsl:attribute name=\"target\">_blank</xsl:attribute>\r\n                    <xsl:value-of select=\"title\"/>\r\n                </a>\r\n            </td>\r\n        </tr>\r\n        <tr>\r\n            <td>\r\n                <xsl:if test=\"$description\">\r\n                    <xsl:value-of select=\"$description\"/>\r\n                </xsl:if>\r\n            </td>\r\n        </tr>\r\n    </xsl:template>\r\n</xsl:stylesheet>');
/*!40000 ALTER TABLE `STYLESHEET` ENABLE KEYS */;

 


--
-- Insert basic channel
--
 /*!40000 ALTER TABLE `CHANNEL` DISABLE KEYS */;
INSERT INTO `CHANNEL` (`ID`,`STYLESHEET_ID`,`NOTIFICATION_TEMPLATE_ID`,`CREATOR`,`TITLE`,`DESCRIPTION`,`LAST_HARVEST`,`MODE`,`FEED_URL`,`REFRESH_DELAY`,`CREATION_DATE`,`CSTATUS`,`SECONDARY_ID`,`LANGUAGE_ID`) VALUES 
 (1,2,1,1,'Obligations statistics','Obligations per year diagram for interval 2001-2009','1990-02-01 00:00:00','PULL','http://rod.eionet.eu.int/events.rss',120,UTC_TIMESTAMP(),1,'3b0f5f8db66da74d3acbca0e0acdd6d84539b010','en');
/*!40000 ALTER TABLE `CHANNEL` ENABLE KEYS */;



--
-- Insert default delivery type for basic channel
--

/*!40000 ALTER TABLE `CHANNEL_DELIVERY_TYPE` DISABLE KEYS */;
INSERT INTO `CHANNEL_DELIVERY_TYPE` (`CHANNEL_ID`,`DELIVERY_TYPE_ID`) VALUES 
 (1,3);
/*!40000 ALTER TABLE `CHANNEL_DELIVERY_TYPE` ENABLE KEYS */;



/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

