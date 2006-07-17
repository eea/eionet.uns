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


import os
from xml.dom.minidom import parse,Document


UNS_RESOURCE_HOME=os.environ.get( 'UNS_RESOURCE_HOME' )
message=None
if not UNS_RESOURCE_HOME: message='You need to specify UNS_RESOURCE_HOME system variable'
if message:
    print message
    sys.exit(1)


uns_config_file =  os.path.join(UNS_RESOURCE_HOME,"uns-config.xml")

uns_config_dom  = parse(open(uns_config_file))


class ConfigElement:
        
    def __init__(self,name,owner):
        self._name = name
        self.owner = owner        
        self._dict = {}

    def __getitem__(self, key):
        return self._dict[key]
        
    def __setitem__(self, key, value):
        self._dict[key] = value        
        el =  uns_config_dom.getElementsByTagName(self._name)[0]
        pr =  el.getElementsByTagName(key)[0].firstChild.data = str(value)
        f = open(uns_config_file, "wb")
        f.write(uns_config_dom.toxml(encoding="utf-8"))
        f.close()
                
    def __len__(self):
        return len(self._dict)        

    def keys(self):
        return self._dict.keys()



class Config:

    def loadConfig(self,parent,level):
        for element in parent.childNodes:
            parent = self._getParent(element,level)
            if(element.hasChildNodes()):
                setattr(parent,element.tagName,ConfigElement(element.tagName,parent))
                self.loadConfig(element,level+1)
            elif(element.data.strip() and element.nodeName != "#comment"):
                parentNode = element.parentNode
                getattr(parent.owner.owner,parentNode.parentNode.tagName)[parentNode.tagName] = element.data            
    

    def _getParent(self,element,level):
        parent = self
        names = []
        for i in range(level):
            element = element.parentNode
            names.append(element.tagName)
        names.reverse()
        for name in names:
            parent = getattr(parent,name)
        return parent
            
                                                         
                                                         
config = Config()
#import pdb;pdb.set_trace()
config.loadConfig(uns_config_dom.documentElement, 0)
logger_conf=config.logger
dbserver = config.dbserver
jabberserver = config.jabberserver
smtpserver = config.smtpserver
pop3server = config.pop3server
notificator_conf = config.daemons.notificator
harvester_conf = config.daemons.harvester
feed = config.feed
uns_daemons = config.daemons


dbserver["port"] = int(dbserver["port"])
dbserver["connect_timeout"] = int(dbserver["connect_timeout"])
jabberserver["port"] = int(jabberserver["port"])
smtpserver["smtp_port"] = int(smtpserver["smtp_port"])
pop3server["pop3_port"] = int(pop3server["pop3_port"])
harvester_conf['interval']=int(harvester_conf['interval'])
harvester_conf['pull_threads']=int(harvester_conf['pull_threads'])
notificator_conf['interval']=int(notificator_conf['interval'])



    

                        