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

import threading, sys, string

from UNS.util.ORMPY.Session import Session
from UNS.ERA.Event import Event
from UNS.ERA.Channel import Channel
from UNS.ERA.EventMetadata import EventMetadata
from UNS.util.RDFUtil import RDFCollector
from UNS.util.ORMPY.SqlFunction import MySqlFunction
from UNS.util.ORMPY.exceptions import *
from UNS.Logging import getLogger

logger=getLogger("UNS.daemons.EventsPicker")

class Puller(threading.Thread):
    """ """
    def __init__(self, channel):
        threading.Thread.__init__(self)
        self.channel=channel
    
    def run(self):
        try:
            self.__pull()
        except: #shall not happen
            logger.critical(str(sys.exc_info()[1]))
        
        
    def __pull(self):
        """
        For a given channel as argument this method pulls the external 
        web service channel content, parses it, creates a list of events objects 
        and stores their data in the UNS database
        """
        session=None
        try:
            session=Session()
            self.channel['LAST_HARVEST']=MySqlFunction('UTC_TIMESTAMP()')
            result=session.update(self.channel)
            session.commitTransaction()
            events=self.gather()
            self.insertEvents(session,events)
        except Exception, e:
            logger.info("Harvesting events data through \"%s\" channel  FAILED !" % self.channel['TITLE'])
            logger.exception(e)
            if session: session.rollbackTransaction()
                
        if session: session.close()
            
    def gather(self, rdf=None):
        """ Gets array of teh RDFThing objects"""
        c=RDFCollector()
        events=[]
        try:
            if rdf:
                events=c.getEventsC(rdf)
            else:
                events=c.getEvents(self.channel['FEED_URL'])
        except Exception, e:
            raise IOError(e)
        return events

    def insertEvents(self, session, events):
        """ Inserts harvested events through the specified channel """
        counter=0
        for event in events:
            try:
                self.__insert(session, event)
                counter+=1
                session.commitTransaction()
            except ORMPyIntegrityError:
                """ Nothing to do event already exists"""
            except Exception, e:
                session.rollbackTransaction()
                logger.error("Inserting of the New Event: %s FAILED !" % event.getSubject())
                logger.exception(e)
        
        logger.info("Collected %d new events through \"%s\" channel" % (counter, self.channel['TITLE']))
                
    def __insert(self, session, event):
        """ Inserts single event and all its metadata """
        ev=Event()
        ev['CHANNEL_ID']=self.channel['ID']
        ev['EXT_ID']=event.getSubject()
        ev['CREATION_DATE']=MySqlFunction('UTC_TIMESTAMP()')
        ev['RTYPE']=event.getType()
        ev['PROCESSED']=0
        session.create(ev)
        elements=event.getMetadata()
        for element in elements:
            values=elements[element]
            for value in values:
                evmd=EventMetadata()
                evmd['EVENT_ID']=ev['ID']
                evmd['PROPERTY']=element
                evmd['VALUE']=value.encode("UTF-8")
                session.create(evmd)


    
    
            
        
            
            
            
            
                
            