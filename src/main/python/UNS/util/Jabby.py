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

from UNS.util.jabber.client import *
from UNS.util.jabber.protocol import *
from UNS.Logging import getLogger
logger=getLogger("UNS.Jabby")

class Jabby:
    """ """
    
    def __init__(self):
        self.jabberSession = None
        self.awareDisconnect = False
        
    def connect(self, host, port , username, password):
        try:
            conn = Client(host, debug=[])
            conn.UnregisterDisconnectHandler(conn.DisconnectHandler)
            conn.RegisterDisconnectHandler(self.__unsDisconnect)
            con_type = conn.connect((host, port))
            if not con_type:
                raise Exception , 'Unable to connect to the jabber server "%s" !' % host 
        except IOError, e:
            logger.exception(e)
            raise e
        
        if not conn.auth(username, password,'UNS',1):       
                raise IOError('Can not auth with jabber server ! Please check the UNS jabber configuration')
        else: self.jabberSession=conn
        
    def disconnect(self):
        """ UNS Aware disconnect from the Jabber server """
        self.awareDisconnect=True
        self.jabberSession.disconnect()
        
    def __unsDisconnect(self):
        """ Called when UNS is disconnected from the Jabber server """
        logger.debug("Disconnected from the jabber server !")
        if not self.jabberSession:
            return
        self.jabberSession = None
        if not self.awareDisconnect:
            logger.critical("CONNECTION WITH JABBER SERVER IS LOST !!!")
        self.awareDisconnect = False
        
    def sendMessage(self, jid, subject=None, message=None, messageType=None):
        self.jabberSession.send(Message(jid, message,messageType,subject))

