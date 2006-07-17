#!/bin/sh
#
# unsd:  Script that controls the UNS daemon process


# Location of the Python executable
PYTHON="/usr/bin/python"

# Location of Your Zope instance
INSTANCE_HOME="/opt/nedjo/zope/zope-2.8.2/uns_instance"

#WEB context root of the Your UNS installation
UNS_ROOT_CONTEXT="http://uns.eionet.eu.int"



UNSD_HOME=$INSTANCE_HOME/Products/UNS
export INSTANCE_HOME UNSD_HOME PYTHON UNS_ROOT_CONTEXT

RETVAL=0

start() {
    exec $PYTHON $UNSD_HOME/unsd.py -h $INSTANCE_HOME
}

stop() {
    UNSD_PID_FILE=$INSTANCE_HOME/log/unsd.pid
    UNSD_PID=`cat $UNSD_PID_FILE`
    rm -f $UNSD_PID_FILE
    kill $UNSD_PID
}


case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    *)
        echo $"Usage: $0 {start|stop|restart}"
        ;;
esac
exit $RETVAL





