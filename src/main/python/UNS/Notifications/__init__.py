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

import  poplib
import cStringIO
import re

from UNS.Config import *
from UNS.util import *
from UNS.util.EMaily import POPy
from UNS.ERA.Delivery import Delivery
from UNS.queries.qnotify import qFailedMail
from UNS.Logging import getLogger
from UNS.ERA.Event import Event
from UNS.ERA.Channel import Channel
from UNS.ERA.Subscription import Subscription
from UNS.ERA.EEAUser import EEAUser
from UNS.Notifications.embedded_code import executeTemplate        


logger=getLogger("UNS.daemons.Notificator")

TITLE=["http://purl.org/rss/1.0/title", "http://purl.org/dc/elements/1.1/title"]
DESCRIPTION=["http://purl.org/rss/1.0/description", "http://purl.org/dc/elements/1.1/description"]

http_link = re.compile('https?://')
html_link = '<a href="%s">%s</a>'

def prepareText(template, event, subscription, homeURL=''): 
    """ 
    Order while filling placeholders is very important.
    One UNS placeholder is not substring of another.
    Example:  - $EVENT.DATE must be replaced before $EVENT. 
    0 - Predicate
    1- Object
    """
    event_title=' '
    metadata=event['META_DATA_TUPLES']
    fr=filter(lambda mel: mel[0] in TITLE ,metadata)
    sc=filter(lambda mel: mel[0] in DESCRIPTION ,metadata)
    rs=filter(lambda mel: not mel[0] in TITLE and not mel[0] in DESCRIPTION ,metadata)
    metadata=fr+sc+rs
    
    if len(fr)>0: event_title=fr[0][1]
    subj=template['SUBJECT']
    ev_creation_date=event['CREATION_DATE'].strftime('%Y-%m-%dT%H:%M:%SZ')
    
    content = {}
    content["PLAIN"] = template['TEXT_PLAIN']
    content["HTML"] = subscription['PREFER_HTML'] and template['TEXT_HTML'] or None

    for kind, text in content.items():
        if (text):
            isHTML = (kind == "HTML")
            text=text.replace("$EVENT.DATE", ev_creation_date)
            text=text.replace("$USER",str(subscription['USER_FULL_NAME']))
            text=text.replace("$EVENT.TITLE",event_title)
            text=text.replace("$EVENT.CHANNEL",subscription['CHANNEL_NAME'])
            if text.count("$UNSUBSCRIBE_LINK"):
                unsub_link = homeURL+"/subscriptions/unsubscribe.jsf?subsc="+str(subscription['SECONDARY_ID'])
                if isHTML: unsub_link = html_link % (unsub_link,unsub_link)
                text=text.replace("$UNSUBSCRIBE_LINK",unsub_link)
            # To be phased out
            if text.count("$UNSUSCRIBE_LINK"):
                unsub_link = homeURL+"/subscriptions/unsubscribe.jsf?subsc="+str(subscription['SECONDARY_ID'])
                if isHTML: unsub_link = html_link % (unsub_link,unsub_link)
                text=text.replace("$UNSUSCRIBE_LINK",unsub_link)
            nl= isHTML and "<br/>" or "\n" 
            event_body = cStringIO.StringIO()
            for mel in metadata:
                event_body.write(getLocalName(mel[0]).upper())
                event_body.write(": ")
                value = mel[1]
                if isHTML:
                    value = value.replace("\n","<br/>")                 
                    if http_link.match(value):
                        value = html_link % (value,value)
                event_body.write(value)
                event_body.write(nl)
            content[kind]= text.replace("$EVENT", event_body.getvalue())
            event_body.close()
    
    subj=subj.replace("$EVENT.CHANNEL",subscription['CHANNEL_NAME'])
    subj=subj.replace("$EVENT.TITLE",event_title)
    subj=subj.replace("$USER",str(subscription['USER_FULL_NAME']))
    subj=subj.replace("$EVENT.DATE",ev_creation_date)
    
    
    user = EEAUser()
    user['fullName'] = subscription['USER_FULL_NAME']
    user['externalId']  = subscription['EXT_USER_ID']
    
    channel = Channel()
    channel['title'] = subscription['CHANNEL_NAME']
    #channel['creator'] = 'Creator - not included in prototype'
    

    subs = Subscription();
    subs['channel'] = channel
    subs['user'] = user

    event_metadata = {}
    metadata_dict = {}
    metadata_list = []
    for mel in metadata:
        event_metadata[mel[0]] = mel[1]
        metadata_dict[mel[0]] = metadata_dict.get(mel[0], []) + [mel[1]]
        metadata_list.append(list(mel[0]))
    
    event = Event()
    event['date'] = ev_creation_date
    event['title'] = event_title
    event['metadata'] = event_metadata
    
    
    templ_namespace = {}    
    templ_namespace["subscription"] = subs
    templ_namespace["event"] = event
    templ_namespace["metadata_dict"] = metadata_dict
    templ_namespace["metadata_list"] = metadata_list


    if (content["PLAIN"] != None):
        try:
            content["PLAIN"] = executeTemplate(content["PLAIN"], templ_namespace,False)
        except Exception,err:
            logger.exception(err)
    if (content["HTML"] != None):
        try:
            content["HTML"] = executeTemplate(content["HTML"], templ_namespace,True)
        except Exception,err:
            logger.exception(err)

        
    return subj, content["PLAIN"], content["HTML"]

def checkFailedMails(session):
    """ Checks and delete faiiled mails returned to the admin account 
        and change status of corespondendig delivery""" 
    server = None
    try:
        try:
            popy = POPy()
            popy.connect(pop3server['pop3_host'],pop3server['pop3_port'],pop3server['pop3_username'],pop3server['pop3_password'])  
            server = popy.pop3Session
            (numMsgs, totalSize) = server.stat()
            for i in range(1, numMsgs + 1):
                (header, msg, octets) = server.retr(i)
                for line_index in range(0,len(msg)):
                    uns_header_index = msg[line_index].find("X-UnsId:") 
                    if(uns_header_index != -1):
                        notification_id = long(msg[line_index][uns_header_index + 8:].strip())
                        logger.info("Founded faiiled notification with id: %d " % (notification_id,))
                        try:
                            result = session.findCustom(Delivery,qFailedMail,[notification_id])
                            if(result):
                                delivery = result[0]
                                delivery['DELIVERY_STATUS']=0
                                session.update(delivery)
                                session.commitTransaction()                        
                        except Exception, e:
                            logger.exception(e)
                        break
                server.dele(i)                                    
        except Exception, e:
            logger.exception(e)
    finally:
        if(server):popy.disconnect()        
