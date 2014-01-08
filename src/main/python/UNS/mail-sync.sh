#!/bin/sh
#
# mail-sync:  Script that controls the UNS daemon process


# Location of the Python executable
PYTHON="/usr/bin/python"

# Location of Your Zope instance
INSTANCE_HOME="@UNS_HOME@/uns-python-source"

#WEB context root of the Your UNS installation
UNS_ROOT_CONTEXT=http://uns.eionet.europa.eu/			  


#UNS home folder 
UNS_HOME=@UNS_HOME@

UNSD_HOME=$INSTANCE_HOME/UNS
export INSTANCE_HOME UNSD_HOME PYTHON UNS_ROOT_CONTEXT UNS_HOME

$PYTHON $UNSD_HOME/mail-sync.py





