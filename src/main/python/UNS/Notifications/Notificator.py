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

import socket, sys, string
socket.setdefaulttimeout(15.0)
from sets import Set
from threading import RLock
from UNS.Config import *
from UNS.Notifications import *
from UNS.util.UnsDaemon import UnsDaemon
from UNS.UNSConstants import DEAMON_DOWN,DEAMON_UP,DEAMON_STARTING,DEAMON_STOPING
from UNS.util.ORMPY.Session import Session
from UNS.util.ORMPY.BasePopo import BasePopo
from UNS.ERA.Notification import Notification
from UNS.ERA.Subscription import Subscription
from UNS.ERA.Filter import Filter
from UNS.ERA.NotificationTemplate import NotificationTemplate
from UNS.ERA.Event import Event
from UNS.ERA.EventMetadata import EventMetadata
from UNS.queries.qnotify import *
from UNS.Notifications.DeliveryHandler import *
from UNS.util.EMaily import POPy
from UNS.util.ORMPY.exceptions import *

from UNS.Logging import getLogger
logger=getLogger("UNS.daemons.Notificator")

class Notificator:
    """
    Represents controller of the Notifications daemon
    """
    
    def __init__(self):
        sync =  RLock()
        self._acquire = sync.acquire
        self._release = sync.release
        self._scanner=None
        self._homeURL=None
    
    def automaticScan(self, delay=10):
        """
        Creates an instance of the Notification Daemon and starts a thread,
        responsible for scanning new events in the background 
        every 'delay' seconds.
        """
        self._acquire()
        if self.getState()==DEAMON_DOWN:
            self._scanner=UnsDaemon(self, delay)
            self._scanner.setDaemon(1)
            self._scanner.setName("Notificator")
            self._scanner.start()
        self._release()
        
    def stopScanner(self):
        self._acquire()
        self._scanner.gracefulStop()
        self._release()
        
    def setHomeURL(self,url):
        self._homeURL = url
        
    def changeNotificatorInterval(self, newinterval):
        if(self._scanner != None):
            self._scanner.setDelay(newinterval)
    
    def getState(self):
        if(self._scanner is None):
            return DEAMON_DOWN
        else:
            return self._scanner.getState()
            
    def manualScan(self):
        """ """
        if not self.getState():
            self.execute()
            return True
        else:
            return False
            
    def execute(self):
        """ 
        Performs next activities:
            - Checks if there are returned(failed) e-mails
            - Checks if there are vacation flags that need to be automatically disabled
            - Scans database for new events and generates new notifications
            - Delivers new notifications and retires failed one
        """
        logger.info("EXECUTING NOTIFICATIOR PROCESS ...")
        session=None
        try:
            session=Session()
            #checkFailedMails(session)
            self.__checkVacationFlags(session)
            self.__scan(session)
            self.__deliver(session)
            logger.debug("THE END !")
        except:
            logger.exception(str(sys.exc_info()[0]))
        if session: session.close()
        logger.info("NOTIFICATIOR PROCESS COMPLETED")
        

    def __checkVacationFlags(self, session):
        """ Search for vacation flags which need to be automatically disabled and disables them """
        try: session.customUpdate(qUnsetVacations)
        except Exception, e: logger.exception(e)
            
    def __scan(self, session):
        """
        Scans for the new events arrvied to the UNS for which notifications
        shall be generated in context of the existing subscriptions
        """
        i=0
        try:
            channels=self.__getUnprocessedEvents(session)
            session.customUpdate(qSetProcessed)
            session.commitTransaction()
            for channelId in channels.keys():
                subscriptions=self.__subscriptions(channelId, session)
                logger.debug("%d subscriptions for channel %d" % (len(subscriptions), channelId))
                events=channels[channelId]
                channel_template=session.findCustom(NotificationTemplate, qChannelTemplate, [channelId],['SUBJECT','TEXT_PLAIN','TEXT_HTML'])[0]
                
                for event in events:
                    try:
                        for subs in subscriptions: 
                            if subs['CREATION_DATE']<event['CREATION_DATE'] and subs['VACATION_FLAG']==0 and self.__checkFilters(event, subs):
                                if self.__generateNotification(session, event, subs, channel_template): i+=1
                        self.__markProcessed(session, event)
                        session.commitTransaction()
                    except:
                        session.rollbackTransaction()
                        logger.exception(str(sys.exc_info()[0]))
            logger.info("Generated %d notifications" % i)
        except Exception, e:
            logger.exception(e)
    
    
    def __deliver(self, session):
        """ 
        Finds new and failed notifications and delivers them.
        Executes delivery process in four parallel threads, one for each delivery type.
        """
        email_messages,jabber_messages,wdb_messages,feed_messages=[], [], [], []
        try:
            for_fetch=['ID', 'SUBJECT', 'CONTENT', 'HTML_CONTENT', 'CHANNEL_ID', 'EVENT_ID', 'EEA_USER_ID', 'DELIVERY_TYPE_ID', 'DELIVERY_ADDRESS', 'FAILED']
            new_items=session.findCustom(BasePopo, qNewNotifications, None, for_fetch)
            failed_items=session.findCustom(BasePopo, qFailedDeliveries, None, for_fetch)
            for_deliver=failed_items+new_items
            nl, fl=len(new_items), len(failed_items)
            for delivery in for_deliver:
                dtid=delivery['DELIVERY_TYPE_ID']
                if dtid==1: email_messages.append(delivery)
                elif dtid==2: jabber_messages.append(delivery)
                elif dtid==3: wdb_messages.append(delivery)
                elif dtid==4: feed_messages.append(delivery)

            nrd=len(email_messages)+len(jabber_messages)+len(wdb_messages)
            logger.info("Sending %d deliveries. Failed nr: %d" % (nrd,fl))
            deilveryBoys=[EMAIL(email_messages), JABBER(jabber_messages), FEED(feed_messages)]
            for sender in deilveryBoys: 
                try: sender.start()
                except:logger.exception(sys.exc_info()[0])
            for sender in deilveryBoys: 
                try: sender.join()
                except:logger.exception(sys.exc_info()[0])

        except Exception, e:
            logger.exception(e)
            
        
    def  __checkFilters(self, event, subscription):
        ok=True
        event_md=Set(event['META_DATA_TUPLES'])
        filters=subscription['FILTERS']
        if filters and event_md:
            ok=False
            for user_filter in filters:
                statments=Set(user_filter['STATMENT_TUPLES'])
                ok=ok or event_md.issuperset(statments)
        return ok
        
        
    def __generateNotification(self, session, event, subs, template):
        """ Generates new notifications for the subscribed users """
        result=True
        notification=Notification()
        try:
            notification['EVENT_ID'], notification['CHANNEL_ID']=event['ID'], event['CHANNEL_ID']
            notification['EEA_USER_ID']=subs['EEA_USER_ID']
            notification['SUBJECT'], notification['CONTENT'], notification['HTML_CONTENT']  = prepareText(template, event, subs, self._homeURL)            
            session.create(notification)
            session.commitTransaction()
        except ORMPyIntegrityError:
                """ Nothing to do notification already exists"""
                result=False
        return result

    def __getUnprocessedEvents(self,session):
        """ Gets all events and users we need to generate notifications for """
        result={}
        
        unprocessed_data=session.findCustom(BasePopo, qUnprocessedEvents, None, ['EVENT_ID', 'CREATION_DATE', 'CHANNEL_ID',  'EXT_ID', 'RTYPE', 'PROPERTY', 'VALUE'])
        temp={}
        for new_item in unprocessed_data:
            eid, cid, channel_events =new_item['EVENT_ID'], new_item['CHANNEL_ID'], []
            try:
                channel_events=result[cid]
            except KeyError:
                result[cid] =channel_events
            
            prop, val=new_item['PROPERTY'], new_item['VALUE']
            try: 
                event=temp[eid]
            except KeyError: 
                event=Event()
                event['ID'], event['CHANNEL_ID'], event['CREATION_DATE']=eid, new_item['CHANNEL_ID'], new_item['CREATION_DATE']
                event['EXT_ID'], event['RTYPE']=new_item['EXT_ID'], new_item['RTYPE']
                temp[eid]=event
                channel_events.append(event)
                
            if prop and val:
                if event['META_DATA_TUPLES']: event['META_DATA_TUPLES'].append((prop, val))
                else: event['META_DATA_TUPLES']=[(prop, val)]

        s=0
        for a in result.keys(): s+=len(result[a])
        logger.info("Processing %d new events..."  % s)
       
        return result
    
    def __subscriptions(self, channelId, session):
        """ Gets all subscriptions for the specifed channel """
        result={}
        channel_subscriptions=session.findCustom(BasePopo, qChannelSubDetails, [channelId, channelId], ['SUBSCRIPTION_ID', 'CREATION_DATE', 'SECONDARY_ID', 'EEA_USER_ID', 'EXT_USER_ID','USER_FULL_NAME', 'VACATION_FLAG','PREFER_HTML', 'CHANNEL_NAME', 'FILTER_ID' ,'PROPERTY', 'VALUE'])
        temp={}
        for new_item in channel_subscriptions:
            sid, fid=new_item['SUBSCRIPTION_ID'], new_item['FILTER_ID']
            try:
                subscription=result[sid]
            except KeyError:
                subscription=Subscription()
                subscription['ID']=sid
                subscription['EEA_USER_ID']=new_item['EEA_USER_ID']
                subscription['USER_FULL_NAME']=new_item['USER_FULL_NAME']
                subscription['VACATION_FLAG']=new_item['VACATION_FLAG']
                subscription['CHANNEL_NAME']=new_item['CHANNEL_NAME']
                subscription['SECONDARY_ID']=new_item['SECONDARY_ID']
                subscription['PREFER_HTML']=new_item['PREFER_HTML']
                subscription['CREATION_DATE']=new_item['CREATION_DATE']
                subscription['FILTERS']=[]
                result[sid] = subscription
            
            if fid:
                prop, val=new_item['PROPERTY'], new_item['VALUE']
                try: 
                    filt=temp[fid]
                except KeyError: 
                    filt=Filter()
                    filt['FILTER_ID'], filt['ID']=fid, new_item['ID']
                    temp[fid]=filt
                    subscription['FILTERS'].append(filt)
                    
                if prop and val:
                    if filt['STATMENT_TUPLES']: filt['STATMENT_TUPLES'].append((prop, val))
                    else: filt['STATMENT_TUPLES']=[(prop, val)]
       
        return result.values()
        
    def __markProcessed(self, session, event):
        ev=Event()
        ev['ID'], ev['CHANNEL_ID'], ev['EXT_ID']=event['ID'],event['CHANNEL_ID'], event['EXT_ID']
        ev['CREATION_DATE'], ev['RTYPE'], ev['PROCESSED']=event['CREATION_DATE'], event['RTYPE'], 1
        session.update(ev)
            
            
        
   
