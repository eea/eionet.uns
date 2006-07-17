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
#   Original code: Nedeljko Pavlovic (ED)
#                  Sasa Milosavljevic(ED) 

npt_user_groups_q = "from NPT_ROLE where NOTIFICATION_PROFILE_TYPE_ID=%s"
npt_delivery_types_q = "from DELIVERY_TYPE where ID IN (select DELIVERY_TYPE_ID from NPT_DT where NOTIFICATION_PROFILE_TYPE_ID=%s)"
available_channels_g = "from CHANNEL where NOTIFICATION_PROFILE_TYPE_ID is NULL"
npt_channels_g = "from CHANNEL where NOTIFICATION_PROFILE_TYPE_ID = %s"
existing_subscription_q = "select count(*) as COUNT from SUBSCRIPTION where CHANNEL_ID IN (select ID from CHANNEL where NOTIFICATION_PROFILE_TYPE_ID =%s)"

