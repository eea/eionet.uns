--
-- Fixed collation on the HTML_CONTENT column. It is now utf8.  
-- This fix is also integreted in the UNS2-create.sql script. 
--
ALTER TABLE `NOTIFICATION` MODIFY COLUMN `HTML_CONTENT` TEXT CHARACTER SET utf8 COLLATE utf8_general_ci;