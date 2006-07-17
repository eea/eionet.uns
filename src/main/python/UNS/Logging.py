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


import logging, logging.handlers
from os import environ
instance_home = environ.get('INSTANCE_HOME') or environ.get('ZOPE_HOME')
if __name__=="__main__": sys.path.append(instance_home)
from UNS.Config import logger_conf

default_level=logging.DEBUG
if logger_conf['level']: default_level=getattr(logging,logger_conf['level'])
formatter = logging.Formatter("[%(asctime)s] [%(levelname)s] %(message)s")
formatter2 = logging.Formatter("[%(asctime)s] [%(levelname)s] %(message)s")

default_fh=logging.handlers.RotatingFileHandler("%s/log/%s" % (instance_home,logger_conf['uns_log']), maxBytes=10000000, backupCount=10)
default_fh.setLevel(default_level)
default_fh.setFormatter(formatter)

daemons_fh=logging.handlers.RotatingFileHandler("%s/log/%s" % (instance_home,logger_conf['unsd_log']), maxBytes=10000000, backupCount=10)
daemons_fh.setLevel(default_level)
daemons_fh.setFormatter(formatter2)

if not logging.getLogger('UNS').handlers:
    logging.getLogger('UNS').addHandler(default_fh)
    logging.getLogger('UNS').propagate = False
if not logging.getLogger('UNS.daemons').handlers:
    logging.getLogger('UNS.daemons').addHandler(daemons_fh)
    logging.getLogger('UNS.daemons').propagate = False


def getLogger(name):
    log = logging.getLogger(name)
    log.setLevel(default_level)
    return log
    

    


