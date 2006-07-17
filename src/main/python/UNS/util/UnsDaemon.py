import os, time, string, sys
from threading import RLock, Thread
import datetime
from UNS.UNSConstants import DEAMON_DOWN,DEAMON_UP,DEAMON_STARTING,DEAMON_STOPING
from UNS.Logging import getLogger

logger=getLogger("UNS.daemons.UnsDaemon")

class UnsDaemon(Thread):
    """
    This class is a THREAD responsible for dispatchoingbackgrounf job
    every 'delay' seconds. This is not a user-level class.
    """
    def __init__(self, command, delay):
        Thread.__init__(self) 
        self._delay=delay
        self._controller=command
        self.running=False
        self.kill=False

    def run(self):
        self.running=True
        
        while self.running:
            logger.debug("===== EXECUTING "+self.getName()+" DAEMON =====" )
            try:
                self._controller.execute()
            except:
                logger.critical("ERROR %s CATCHED INSIDE DAEMON !" % str(sys.exc_info()[0]))
            if(self.kill): self.__checkStopRequest()
            else: self.wait()
                
    def wait(self):
        w=self._delay*60
        while self.running and w>0 :
            time.sleep(10)
            w=w-10
            if(self.kill): self.__checkStopRequest()
            
    def gracefulStop(self):
        self.kill=True
        
    def setDelay(self, d):
        self._delay=d
        
    def getDelay(self):
        return self._delay
        
    def isRunning(self):
        return self.running
        
    def __checkStopRequest(self):
         if(self.kill): 
                self.running=False
    
    def getState(self):
        if(self.running and self.kill):
            return DEAMON_STOPING
        
        elif(not self.running and self.kill):
            return DEAMON_DOWN
        
        elif(self.running and not self.kill):
            return DEAMON_UP
        
        elif(not self.running and not self.kill):
            return DEAMON_STARTING