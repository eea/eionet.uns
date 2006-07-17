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

import os, time, datetime, string, sys
import string, xmlrpclib, httplib, threading

from UNS.util.ORMPY.Session import Session
from UNS.util.ORMPY.BasePopo import BasePopo
from UNS.ERA.Notification import Notification
from UNS.ERA.Delivery import Delivery
from UNS.ERA.Event import Event
from UNS.ERA.EventMetadata import EventMetadata
from UNS.util.ORMPY.SqlFunction import MySqlFunction
from UNS.Config import *
from UNS.util.Jabby import Jabby
from UNS.util.EMaily import EMaily
from UNS.util.XmlRpc import BasicAuthTransport
from UNS.queries.qnotify import *
from UNS.util.RDFUtil import RDFGenerator
from UNS.util.RDFThing import RDFThing
from UNS.Logging import getLogger

logger=getLogger("UNS.daemons.DeliveryBoy")

class DeliveryBoy(threading.Thread):
    """ """
    
    def __init__(self, notifications):
        threading.Thread.__init__(self)
        self.notifications=notifications
        
    def connect(self): pass
    def disconnect(self): pass
    def send(self, address, notification): pass
    
    def run(self):
        session=None
        try:
            self.connect()
            session=Session()
            for item in self.notifications: 
                try: self.__deliver(session, item)
                except: logger.exception(str(sys.exc_info()[0]))
        except Exception, e:
            logger.exception(e)
            
        if session: session.close()
        try: self.disconnect()
        except Exception, e: logger.exception(e)
        
        
    def __deliver(self, session, item):
        try:
            delivery=Delivery()
            delivery['NOTIFICATION_ID']=item['ID']
            delivery['DELIVERY_TYPE_ID']=item['DELIVERY_TYPE_ID']
            delivery['DELIVERY_STATUS']=0
            delivery['DELIVERY_TIME']=MySqlFunction('UTC_TIMESTAMP()')
            if item['FAILED']==0: session.create(delivery)
            try:
                self.send(item['DELIVERY_ADDRESS'], item)
                delivery['DELIVERY_STATUS']=1
            except:
                logger.error("Failed to send notification ID: %d to address %s [%s]" % (item['ID'],item['DELIVERY_ADDRESS'], str(sys.exc_info()[1])))
                #logger.exception(str(sys.exc_info()[0]))
            delivery['DELIVERY_TIME']=MySqlFunction('UTC_TIMESTAMP()')
            session.update(delivery)
            session.commitTransaction()
        except  Exception, e:
            session.rollbackTransaction()
            logger.exception(e)
    
    
class JABBER(DeliveryBoy):
    def __init__(self, notifications):
        DeliveryBoy.__init__(self, notifications)
        self.jabby=Jabby()
    
    def connect(self):
        try: self.jabby.connect(jabberserver['host'], jabberserver['port'], jabberserver['username'], jabberserver['password'])
        except: raise Exception , 'Unable to connect to the jabber server'
    
    def disconnect(self):
        self.jabby.disconnect()
        
    def send(self, address, notification):
        self.jabby.sendMessage(address, notification['SUBJECT'], notification['CONTENT'], jabberserver["jabber_message_type"])
        logger.info("Jabber message sent to: %s" % address)
        time.sleep(1)


class EMAIL(DeliveryBoy):
    def __init__(self, notifications):
        DeliveryBoy.__init__(self, notifications)
        self.emaily=EMaily()
        
    def connect(self):
        try: self.emaily.connect(smtpserver['smtp_host'])
        except: raise Exception , 'Unable to connect to the SMTP server'
        
    def disconnect(self):
        self.emaily.disconnect()
        
    def send(self, address, notification):
        self.emaily.sendMessage(address, notification ,pop3server['adminmail'])
        logger.info("E-Mail message sent to: %s" % address)
        
        
class WDB(DeliveryBoy):
    def __init__(self, notifications):
        DeliveryBoy.__init__(self, notifications)
             
    def send(self, address, notification):
        gen=RDFGenerator()
        eventId=notification['EVENT_ID']
        try:
            session=Session()
            event=session.findByPK(Event,[eventId])
            elements=session.findCustom(EventMetadata,"select * from EVENT_METADATA where EVENT_ID=%s", [eventId])
            thing=RDFThing(event['EXT_ID'], event['RTYPE'])
            for element in elements:
                thing[element['PROPERTY']]=element['VALUE'].decode('UTF-8')
        finally:
            if session: session.close()
        message=gen.generate_rdf_items([thing])
        if address[-1]=="/":  wdbUrl=address+"xml-rpc"
        else: wdbUrl=address+"/xml-rpc"
        d=xmlrpclib.Server(wdbUrl)
        d.WDSXmlRpcService.push("UNS",message)
        logger.info("WDB message sent to: %s" % str(address))
        
        
class FEED(DeliveryBoy):
    def __init__(self, notifications):
        DeliveryBoy.__init__(self, notifications)
   
