# The contents of this file are subject to the Mozilla Public
# License Version 1.1 (the "License"); you may not use this file
# except in compliance with the License. You may obtain a copy of
# the License at http://www.mozilla.org/MPL/
#
# Software distributed under the License is distributed on an "AS
# IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
# implied. See the License for the specific language governing
# rights and limitations under the License.
#
# The Original Code is Reportnet Unified Notification Service
#
# The Initial Owner of the Original Code is European Environment
# Agency (EEA).  Portions created by European Dynamics (ED) company are
# Copyright (C) by European Environment Agency.  All
# Rights Reserved.
#
# Contributor(s):
#   Original code: Sasha Milosavljevic(ED)
#                  
from UNS.queries import cleanUpQuery

subscribed_channels_q = "from SUBSCRIPTION where EEA_USER_ID = (select ID from EEA_USER where EXT_USER_ID = %s )"    
subs_delivery_types_q = "from SUBSCRIPTION_DT where SUBSCRIPTION_ID =%s"
subs_filters_q = "from FILTER where SUBSCRIPTION_ID =%s order by ID"
filter_statements_q = "from STATEMENT where FILTER_ID =%s"

user_q = "from EEA_USER where EXT_USER_ID = %s"
user_addresses_q = "from DELIVERY_ADDRESS where EEA_USER_ID = (select ID from EEA_USER where EXT_USER_ID = %s)"

user_subscribtions_channels_q = "select S.ID as SUBS_ID, S.CREATION_DATE as SUBS_CREATION_DATE,C.NAME, C.DESCRIPTION, C.LAST_HARVEST from CHANNEL C,SUBSCRIPTION S where S.EEA_USER_ID = (select ID from EEA_USER where EXT_USER_ID = %s ) and S.CHANNEL_ID = C.ID"
failed_notifications_for_subscription_q = "select N.ID  from DELIVERY D, NOTIFICATION N,SUBSCRIPTION S  where S.ID = %s and N.EEA_USER_ID = S.EEA_USER_ID and  D.NOTIFICATION_ID = N.ID and  DELIVERY_STATUS = 0  "
subscription_owner_q = "from EEA_USER E,SUBSCRIPTION S where E.EXT_USER_ID = %s and S.ID = %s and E.ID = S.EEA_USER_ID"
subscription_md5_q = "from SUBSCRIPTION  where SECONDARY_ID = %s "

has_events_q = "select count(*) as EVENT_COUNT from EVENT where CHANNEL_ID = %s"
has_choosable_elements_q = "select count(*) as CHOOSABLE_COUNT from CHOOSABLE  where CHANNEL_ID = %s"
find_channel_sid_q ="from CHANNEL where SECONDARY_ID=%s"

available_channels_q = cleanUpQuery(
        """
        select distinct CHANNEL.* 
        from CHANNEL left outer join CHANNEL_ROLE on CHANNEL_ROLE.CHANNEL_ID = CHANNEL.ID 
        where CHANNEL.CSTATUS = 1 
        and CHANNEL_ROLE.CHANNEL_ID is NULL 
        and CHANNEL.ID NOT IN ( select SUBSCRIPTION.CHANNEL_ID from SUBSCRIPTION where SUBSCRIPTION.EEA_USER_ID = ( select EEA_USER.ID from EEA_USER where EEA_USER.EXT_USER_ID = %s)) 
        """
        )


available_channels_with_roles_q = cleanUpQuery(
        """
        select distinct CHANNEL.* 
        from CHANNEL left outer join CHANNEL_ROLE on CHANNEL_ROLE.CHANNEL_ID = CHANNEL.ID 
        where CHANNEL.CSTATUS = 1 
        and (CHANNEL_ROLE.CHANNEL_ID is NULL or CHANNEL_ROLE.EEA_ROLE_ID IN (select EEA_ROLE.ID from EEA_ROLE where EEA_ROLE.EXT_ID IN (%s))) 
        and CHANNEL.ID NOT IN ( select SUBSCRIPTION.CHANNEL_ID from SUBSCRIPTION where SUBSCRIPTION.EEA_USER_ID = ( select EEA_USER.ID from EEA_USER where EEA_USER.EXT_USER_ID = "%s")) 
        """
        )

user_delivery_types_q = cleanUpQuery(
        """
        select distinct DELIVERY_TYPE.* 
        from SUBSCRIPTION, EEA_USER,SUBSCRIPTION_DT,DELIVERY_TYPE  
        where EEA_USER.EXT_USER_ID = %s 
        and SUBSCRIPTION.EEA_USER_ID = EEA_USER.ID 
        and SUBSCRIPTION_DT.SUBSCRIPTION_ID = SUBSCRIPTION.ID 
        and DELIVERY_TYPE.ID = SUBSCRIPTION_DT.DELIVERY_TYPE_ID;                
        """
        )

all_choosable_statements_for_channel_q = cleanUpQuery(
        """
        select  distinct EVENT_METADATA.PROPERTY, EVENT_METADATA.VALUE 
        from EVENT_METADATA , EVENT , CHOOSABLE  
        where EVENT_METADATA.EVENT_ID= EVENT.ID 
        and EVENT.CHANNEL_ID= %s 
        and CHOOSABLE.CHANNEL_ID = EVENT.CHANNEL_ID 
        and EVENT_METADATA.PROPERTY =CHOOSABLE.PROPERTY   
            UNION 
        select distinct STATEMENT.PROPERTY , STATEMENT.VALUE 
        from STATEMENT,FILTER,SUBSCRIPTION,CHANNEL 
        where STATEMENT.FILTER_ID = FILTER.ID 
        and FILTER.SUBSCRIPTION_ID = SUBSCRIPTION.ID 
        and SUBSCRIPTION.CHANNEL_ID = %s  
        order by PROPERTY        
        """
        )


subscription_for_channel_q = cleanUpQuery(
        """
        select SUBSCRIPTION.* 
        from SUBSCRIPTION, EEA_USER  
        where SUBSCRIPTION.CHANNEL_ID = %s 
        and EEA_USER.EXT_USER_ID = %s        
        and SUBSCRIPTION.EEA_USER_ID = EEA_USER.ID 
        """
        )
