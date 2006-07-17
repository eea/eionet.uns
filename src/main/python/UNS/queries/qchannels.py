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
#   Original code: Sasa Milosavljevic(ED)
#                       Nedeljko Pavlovic (ED)

choosable_metadata_q = "from CHOOSABLE where CHANNEL_ID =%s" 
existing_channels_q = "select CH.*,U.EXT_USER_ID ,(select count(*) from SUBSCRIPTION where CHANNEL_ID = CH.ID) as SUBS_COUNT  from CHANNEL CH, EEA_USER U where CH.MODE=%s and CH.CREATOR = U.ID"    
q_find_channel_sid="from CHANNEL where SECONDARY_ID=%s"
distinct_properties_q = "select distinct EM.PROPERTY FROM EVENT E ,EVENT_METADATA EM where EM.EVENT_ID = E.ID and E.CHANNEL_ID=%s"
user_q = "from EEA_USER where EXT_USER_ID =%s" 
rpc_user_channels_q = "select CH.*,(select count(*) from SUBSCRIPTION where CHANNEL_ID = CH.ID) as SUBS_COUNT  from CHANNEL CH, EEA_USER U where U.EXT_USER_ID=%s and CH.CREATOR = U.ID"
existing_filters_q = "select count(*) as COUNT from CHANNEL, SUBSCRIPTION, FILTER, STATEMENT  where STATEMENT.PROPERTY = %s and STATEMENT.FILTER_ID = FILTER.ID and FILTER.SUBSCRIPTION_ID = SUBSCRIPTION.ID and SUBSCRIPTION.CHANNEL_ID = %s"
channel_delivery_types_q = "from DELIVERY_TYPE, CHANNEL_DELIVERY_TYPE where CHANNEL_DELIVERY_TYPE.CHANNEL_ID = %s and DELIVERY_TYPE.ID = CHANNEL_DELIVERY_TYPE.DELIVERY_TYPE_ID;"
channel_user_groups_q = "from CHANNEL_ROLE where CHANNEL_ID=%s"
user_subscripions_for_channel_q = "select CHANNEL.NAME as CHANNEL_NAME, SUBSCRIPTION.ID,EEA_USER.EXT_USER_ID,EEA_USER.CN as USER_FULL_NAME, EEA_USER.PREFER_HTML, DELIVERY_ADDRESS.ADDRESS from CHANNEL,SUBSCRIPTION,EEA_USER,DELIVERY_ADDRESS where CHANNEL.ID = %s and SUBSCRIPTION.CHANNEL_ID = CHANNEL.ID and EEA_USER.ID = SUBSCRIPTION.EEA_USER_ID and DELIVERY_ADDRESS.DELIVERY_TYPE_ID = 1 and DELIVERY_ADDRESS.EEA_USER_ID = EEA_USER.ID"
qUserChannelRoles="select count(*) as RESULT from CHANNEL_ROLE CR, EEA_ROLE R  where CR.CHANNEL_ID=%s and R.ID=CR.EEA_ROLE_ID \
            and R.EXT_ID in (%s)"
qInsertAllDT="insert into CHANNEL_DELIVERY_TYPE (select %s as CHANNEL_ID, DT.ID as DELIVERY_TYPE_ID from DELIVERY_TYPE DT)"
