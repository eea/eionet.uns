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

import getopt, smtplib, os, sys, time, signal, string
INSTANCE_HOME=os.environ.get( 'INSTANCE_HOME' )
UNS_ROOT_CONTEXT=os.environ.get( 'UNS_ROOT_CONTEXT' )
message=None
if not INSTANCE_HOME: message='You need to specify INSTANCE_HOME system variable'
if not UNS_ROOT_CONTEXT: message='You need to specify UNS_ROOT_CONTEXT system variable'
if message:
    print message
    sys.exit(1)

if __name__=="__main__": sys.path.append(INSTANCE_HOME)
from UNS.Config import *
from UNS.util.ORMPY.Session import Session
from UNS.Harvester.Harvester import Harvester
from UNS.Notifications.Notificator import Notificator
from UNS.Notifications.DeliveryHandler import *


harvester=Harvester()
notificator=Notificator()
notificator.setHomeURL(UNS_ROOT_CONTEXT)

if sys.version.split()[0] < '2.3': raise  Error, 'Invalid python version %s' % sys.version.split[0]


try:
    if uns_daemons["manual"] != 'on':
        print "Manual control of the UNS daemon is turned OFF"
        raise Exception, 'Manual control need to be turned ON in order to avoid conflicts with Zope controled daemon'
    session=Session()
    session.close()
    jabb=JABBER(None)
    maily=EMAIL(None)
    jabb.connect()
    jabb.disconnect()
    maily.connect()
    maily.disconnect()
except:
    print "ERROR: %s" % str(sys.exc_info()[1])
    print "Please set up the UNS configuration correctly before starting daemon"
    print "Configuration file: %s/Products/UNS/etc/uns-config.xml" % INSTANCE_HOME
    sys.exit(1)
    


def createDaemon():
    """ Detach a process from the terminal and run it in the  background as a daemon.  """
    
    try:
        pid = os.fork()
    except OSError, e:
        return( ( e.errno, e.strerror ) )
        
    if ( pid == 0 ): 
        # The first child.
        os.setsid()
        signal.signal( signal.SIGHUP, signal.SIG_IGN )
        try:
            # Fork a second child to prevent zombies. 
            pid = os.fork()
        except OSError, e:
            return( ( e.errno, e.strerror ) )
            
        if ( pid == 0 ):      # The second child.
            #sys.stdin.close()
            #sys.stdout.close()
            #sys.stderr.close()
            os.chdir(os.sep)
            os.umask(0)
        else:
            # Exit parent (the first child) of the second child.
            os._exit(0)
    else:
        # Exit parent of the first child.
        os._exit(0)
        
    return(0)
   
def createPidFile():
    try:
        pid = os.getpid()
    except: pass
    else:
        pid_dir = os.path.join( INSTANCE_HOME, 'log' )
        if not os.path.isdir( pid_dir ): os.mkdir( pid_dir )
        pid_file = os.path.join( pid_dir, 'unsd.pid' )
        pid_fd = open( pid_file, 'w' )
        pid_fd.write( '%s\n' %  os.getpid() )
        pid_fd.close()


if __name__ == "__main__":
    if sys.platform != 'win32': 
        retCode = createDaemon()
        createPidFile()
    
    harvester.automaticUpdate(harvester_conf['interval'])
    time.sleep(10)
    notificator.automaticScan(notificator_conf['interval'])
    #wait 1 min until start harvester thread
   
    
    
    while 1: time.sleep(1000)

    
