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
#                  Sasha Milosavljevic(ED) 

from UNS.queries import cleanUpQuery

throughput_report_for_channel_q = cleanUpQuery(
    """
    select date_format(DELIVERY_TIME,%s) as DELIVERY_DATE ,DELIVERY_TYPE_ID , DELIVERY_STATUS ,count(NOTIFICATION_ID) as NOTIFCOUNT 
    from DELIVERY , NOTIFICATION N, EVENT E  
    where DELIVERY_TYPE_ID != 4 
    and DELIVERY_TIME + 0 > %s and DELIVERY_TIME + 0 < %s  
    and NOTIFICATION_ID = N.ID and N.EVENT_ID = E.ID and E.CHANNEL_ID = %s  
    group by DELIVERY_DATE,DELIVERY_STATUS,DELIVERY_TYPE_ID 
    """
    )
    
throughput_report_for_user_q = cleanUpQuery(
    """
    select date_format(DELIVERY_TIME,%s) as DELIVERY_DATE ,DELIVERY_TYPE_ID , DELIVERY_STATUS ,count(NOTIFICATION_ID) as NOTIFCOUNT 
    from DELIVERY , NOTIFICATION N 
    where DELIVERY_TYPE_ID != 4 
    and DELIVERY_TIME + 0 > %s 
    and DELIVERY_TIME + 0 < %s 
    and NOTIFICATION_ID = N.ID 
    and N.EEA_USER_ID = %s 
    group by DELIVERY_DATE,DELIVERY_STATUS,DELIVERY_TYPE_ID 
    """
    )

throughput_report_full_q = cleanUpQuery(
    """
    select date_format(DELIVERY_TIME,%s) as DELIVERY_DATE ,DELIVERY_TYPE_ID , DELIVERY_STATUS ,count(NOTIFICATION_ID) as NOTIFCOUNT  
    from DELIVERY , NOTIFICATION N, EVENT E  
    where DELIVERY_TYPE_ID != 4 
    and DELIVERY_TIME + 0 > %s 
    and DELIVERY_TIME + 0 < %s 
    and NOTIFICATION_ID = N.ID 
    and N.EEA_USER_ID = %s 
    and N.EVENT_ID = E.ID 
    and E.CHANNEL_ID = %s 
    group by DELIVERY_DATE,DELIVERY_STATUS,DELIVERY_TYPE_ID 
    """
    )
    
throughput_report_default_q = cleanUpQuery(
    """
    select date_format(DELIVERY_TIME,%s) as DELIVERY_DATE ,DELIVERY_TYPE_ID , DELIVERY_STATUS ,count(NOTIFICATION_ID) as NOTIFCOUNT  
    from DELIVERY  
    where DELIVERY_TYPE_ID != 4 
    and DELIVERY_TIME + 0 > %s 
    and DELIVERY_TIME + 0 < %s  
    group by DELIVERY_DATE,DELIVERY_STATUS,DELIVERY_TYPE_ID 
    """
    )
    
    
failed_notifications_report_q = cleanUpQuery(
    """
    select  CH.NAME as CH_NAME , EU.EXT_USER_ID ,DT.NAME as DT_NAME ,DA.ADDRESS,SB.ID as SUBS_ID , count(DL.DELIVERY_TYPE_ID) as FAILED_NR 
    from DELIVERY DL, NOTIFICATION NT, EVENT EV, CHANNEL CH , EEA_USER EU ,DELIVERY_TYPE DT , DELIVERY_ADDRESS DA, SUBSCRIPTION SB 
    where DL.DELIVERY_STATUS = 0  
    and DL.DELIVERY_TYPE_ID = DT.ID  
    and DL.NOTIFICATION_ID = NT.ID 
    and NT.EVENT_ID = EV.ID 
    and EV.CHANNEL_ID = CH.ID  
    and NT.EEA_USER_ID = EU.ID   
    and DL.DELIVERY_TYPE_ID = DA.DELIVERY_TYPE_ID 
    and NT.EEA_USER_ID = DA.EEA_USER_ID  
    and NT.EEA_USER_ID = SB.EEA_USER_ID 
    and CH.ID = SB.CHANNEL_ID 
    group by EU.CN,CH_NAME,DL.DELIVERY_TYPE_ID
    """
    )