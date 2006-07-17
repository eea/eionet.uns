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

import cStringIO, sys, string
from threading import RLock

from UNS.Config import *
from UNS.util.UnsDaemon import UnsDaemon
from UNS.UNSConstants import DEAMON_DOWN,DEAMON_UP,DEAMON_STARTING,DEAMON_STOPING
from UNS.util.ORMPY.Session import Session
from UNS.util.ORMPY.SqlFunction import MySqlFunction
from UNS.ERA.Channel import Channel
from UNS.Harvester.Picker import Puller
from UNS.queries.qharvest import *
from UNS.Logging import getLogger

logger=getLogger("UNS.daemons.Harvester")

class Harvester:
    """
    Represents controller of the Harvesting daemon
    """
    
    
    def __init__(self):
        sync =  RLock()
        self._acquire = sync.acquire
        self._release = sync.release
        self._timestamp= 0
        self._updater=None
    
    def automaticUpdate(self,delay=10):
        """
        Creates an instance of the HarvesterDaemon class and starts a harvesting thread
        """
        self._acquire()
        try:
            if self.getState()==DEAMON_DOWN:
                self._updater=UnsDaemon(self, delay)
                self._updater.setDaemon(1)
                self._updater.setName("Harvester")
                self._updater.start()
        except:
            fp = cStringIO.StringIO()
            traceback.print_exc(file=fp)
            message = fp.getvalue()
            fp.close()
            logger.critical("CRITICAL ERROR WHILE STARTING THREAD !"+repr(message))

            
        self._release()
        
    def stopHarvester(self):
        self._acquire()
        self._updater.gracefulStop()
        self._release()
        
    def killHarvester(self):
        pass
 
    def changeInovationInterval(self, newinterval):
        if(self._updater != None):
            self._updater.setDelay(newinterval)

            
    def getState(self):
        if(self._updater == None):
            return DEAMON_DOWN
        else:
            return self._updater.getState()
            
    def manualUpdate(self):
        """ """
        if not self.getState():
            self.execute()
            return True
        else:
            return False
        
    
    def execute(self):
        logger.info("REFRESHING PULL CHANNELS...")
        try:
            self.__refreshAll()
        except:
            logger.exception(sys.exc_info()[0])
        logger.info("REFRESH OPERATION COMPLETED")
        
    def __refreshAll(self):
        """
        """
        PARALLEL_PULLS=harvester_conf['pull_threads']
        channels=None
        try:
            channels=self.__getChannels()
        except Exception, e:
            logger.exception(e)
        
        channelsNr=len(channels)
        if channelsNr>0:
            logger.info("Harvesting throw %d channels" % len(channels))
            steps, remained=channelsNr / PARALLEL_PULLS, channelsNr % PARALLEL_PULLS
            if steps==0: 
                steps=1
                counter=remained
            else: counter=PARALLEL_PULLS
            cc=0
            for step in range(0,steps):
                workers_list=[]
                for i in range(0,counter):
                    worker=Puller(channels[cc])
                    worker.setName("PullerThread_%d" % i)
                    workers_list.append(worker)
                    try: worker.start()
                    except: logger.exception(sys.exc_info()[0])
                    cc+=1
                for busyWorker in workers_list: 
                    try: busyWorker.join()
                    except: logger.exception(sys.exc_info()[0])
        
    def __getChannels(self):
        """
        Gets all channels from UNS database working in PULL mode
        """
        session=None
        try:
            session=Session()
            result=session.findCustom(Channel,qHarvestChannels)
        finally:
            if session is not None: session.close()
        return result
