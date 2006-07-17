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
from UNS.queries import cleanUpQuery


qUnsetVacations=cleanUpQuery(
    """
    update EEA_USER set VACATION_FLAG=0, VACATION_EXPIRATION=NULL 
    where VACATION_FLAG=1 and UTC_TIMESTAMP()>=VACATION_EXPIRATION    
    """
    )

qUnprocessedEvents=cleanUpQuery(
    """
    select E.ID as EVENT_ID, E.CREATION_DATE, E.CHANNEL_ID, E.EXT_ID, E.RTYPE,  EM.PROPERTY, EM.VALUE 
    from EVENT E, EVENT_METADATA EM 
    where E.PROCESSED=0 and E.ID=EM.EVENT_ID
    and exists (select * from SUBSCRIPTION S where S.CHANNEL_ID=E.CHANNEL_ID)
    order by E.CHANNEL_ID
    """
    )

#Marks as processed new events which do not belong to any subscriptions 
qSetProcessed=cleanUpQuery(
    """
    update EVENT E set E.PROCESSED=1
    where E.PROCESSED=0 and not exists (select * from SUBSCRIPTION S where S.CHANNEL_ID=E.CHANNEL_ID)
    """
    )
    
qChannelSubDetails=cleanUpQuery(
    """
    select S.ID as SUBSCRIPTION_ID, S.CREATION_DATE, S.SECONDARY_ID, S.EEA_USER_ID, 
    U.CN  as USER_FULL_NAME, U.VACATION_FLAG , U.PREFER_HTML, U.EXT_USER_ID,  
    (select C.TITLE FROM CHANNEL C where C.ID=%s) as CHANNEL_NAME,
    ST.FILTER_ID, ST.PROPERTY, ST.VALUE  
    from EEA_USER U,SUBSCRIPTION S left outer join FILTER F on S.ID=F.SUBSCRIPTION_ID 
    left outer join STATEMENT ST on F.ID=ST.FILTER_ID
    where U.ID = S.EEA_USER_ID 
    and S.CHANNEL_ID=%s order by S.ID
    """
    )
    
qChannelTemplate=cleanUpQuery(
    """
    select NT.SUBJECT, NT.TEXT_PLAIN, NT.TEXT_HTML from CHANNEL C, NOTIFICATION_TEMPLATE NT 
    where C.ID=%s and C.NOTIFICATION_TEMPLATE_ID=NT.ID
    """
    )
    
qNewNotifications=cleanUpQuery(
    """
    select N.ID, N.SUBJECT, N.CONTENT, N.HTML_CONTENT, S.CHANNEL_ID, N.EVENT_ID, N.EEA_USER_ID, SDT.DELIVERY_TYPE_ID,
    (select ADDRESS from DELIVERY_ADDRESS DA where DA.EEA_USER_ID=N.EEA_USER_ID and DA.DELIVERY_TYPE_ID=SDT.DELIVERY_TYPE_ID) as DELIVERY_ADDRESS,
    0 as FAILED from NOTIFICATION N, SUBSCRIPTION S, SUBSCRIPTION_DT SDT
    where not exists (select * from DELIVERY where N.ID=NOTIFICATION_ID)
    and N.EEA_USER_ID=S.EEA_USER_ID and N.CHANNEL_ID=S.CHANNEL_ID and S.ID=SDT.SUBSCRIPTION_ID
    order by N.ID
    """
    )
    
qFailedDeliveries=cleanUpQuery(
    """
    select N.ID, N.SUBJECT, N.CONTENT, N.HTML_CONTENT, S.CHANNEL_ID, N.EVENT_ID, N.EEA_USER_ID, SDT.DELIVERY_TYPE_ID,
    (select ADDRESS from DELIVERY_ADDRESS DA where DA.EEA_USER_ID=N.EEA_USER_ID and DA.DELIVERY_TYPE_ID=SDT.DELIVERY_TYPE_ID) as DELIVERY_ADDRESS,
     1 as FAILED from NOTIFICATION N, SUBSCRIPTION S, DELIVERY SDT where
    N.EEA_USER_ID=S.EEA_USER_ID and N.CHANNEL_ID=S.CHANNEL_ID and N.ID=SDT.NOTIFICATION_ID
    and SDT.DELIVERY_STATUS=0  order by N.ID
    """
    )

qFailedMail = " from DELIVERY where NOTIFICATION_ID=%s and DELIVERY_TYPE_ID = (select ID from DELIVERY_TYPE where NAME = 'EMAIL')"

qUserFeed = cleanUpQuery(
    """
    select E.ID,  E.EXT_ID, E.RTYPE, EM.PROPERTY, EM.VALUE
    from NOTIFICATION N, DELIVERY D, EVENT E, EVENT_METADATA EM
    where N.EEA_USER_ID=%s and D.DELIVERY_TYPE_ID=4
    and N.ID=D.NOTIFICATION_ID
    and DATE_SUB(UTC_TIMESTAMP(),INTERVAL %s DAY) <= E.CREATION_DATE 
    and E.ID=N.EVENT_ID  and E.ID=EM.EVENT_ID
     """
     )
     
